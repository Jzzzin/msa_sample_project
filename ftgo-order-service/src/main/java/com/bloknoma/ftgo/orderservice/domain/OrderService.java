package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.orderservice.api.events.OrderDetails;
import com.bloknoma.ftgo.orderservice.api.events.OrderDomainEvent;
import com.bloknoma.ftgo.orderservice.api.events.OrderLineItem;
import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;
import com.bloknoma.ftgo.orderservice.domain.value.RevisedOrder;
import com.bloknoma.ftgo.orderservice.domain.exception.InvalidMenuItemIdException;
import com.bloknoma.ftgo.orderservice.domain.exception.OrderNotFoundException;
import com.bloknoma.ftgo.orderservice.domain.exception.RestaurantNotFoundException;
import com.bloknoma.ftgo.orderservice.domain.repository.OrderRepository;
import com.bloknoma.ftgo.orderservice.domain.repository.RestaurantRepository;
import com.bloknoma.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import com.bloknoma.ftgo.orderservice.sagas.createorder.CreateOrderSagaState;
import com.bloknoma.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import com.bloknoma.ftgo.orderservice.web.MenuItemIdAndQuantity;
import com.bloknoma.ftgo.restaurantservice.events.MenuItem;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Transactional
public class OrderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private OrderRepository orderRepository;

    private RestaurantRepository restaurantRepository;

    private SagaManager<CreateOrderSagaState> createOrderSagaManager;

    private SagaManager<CancelOrderSagaData> cancelOrderSagaManager;

    private SagaManager<ReviseOrderSagaData> reviseOrderSagaManager;

    private OrderDomainEventPublisher orderAggregateEventPublisher;

    private Optional<MeterRegistry> meterRegistry;

    public OrderService(OrderRepository orderRepository, DomainEventPublisher eventPublisher, RestaurantRepository restaurantRepository, SagaManager<CreateOrderSagaState> createOrderSagaManager, SagaManager<CancelOrderSagaData> cancelOrderSagaManager, SagaManager<ReviseOrderSagaData> reviseOrderSagaManager, OrderDomainEventPublisher orderAggregateEventPublisher, Optional<MeterRegistry> meterRegistry) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.createOrderSagaManager = createOrderSagaManager;
        this.cancelOrderSagaManager = cancelOrderSagaManager;
        this.reviseOrderSagaManager = reviseOrderSagaManager;
        this.orderAggregateEventPublisher = orderAggregateEventPublisher;
        this.meterRegistry = meterRegistry;
    }

    // 주문 생성
    public Order createOrder(long consumerId, long restaurantId, List<MenuItemIdAndQuantity> lineItems) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);

        // order 생성
        ResultWithDomainEvents<Order, OrderDomainEvent> orderAndEvents = Order.createOrder(consumerId, restaurant, orderLineItems);

        Order order = orderAndEvents.result;
        // order 저장
        orderRepository.save(order);

        // 도메인 이벤트 발행
        orderAggregateEventPublisher.publish(order, orderAndEvents.events);

        OrderDetails orderDetails = new OrderDetails(consumerId, restaurantId, orderLineItems, order.getOrderTotal());

        // order 사가 생성
        CreateOrderSagaState data = new CreateOrderSagaState(order.getId(), orderDetails);
        createOrderSagaManager.create(data, Order.class, order.getId());

        // 주문 카운터
        meterRegistry.ifPresent(mr -> mr.counter("placed_orders").increment());

        return order;
    }

    // 주문 목록 생성
    private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, Restaurant restaurant) {
        return lineItems.stream().map(li -> {
            MenuItem om = restaurant.findMenuItem(li.getMenuItemId()).orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
            return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
        }).collect(toList());
    }

    // 주문 상태 변경
    public Order updateOrder(long orderId, Function<Order, List<OrderDomainEvent>> updater) {
        return orderRepository.findById(orderId).map(order -> {
            orderAggregateEventPublisher.publish(order, updater.apply(order));
            return order;
        }).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    // 주문 승인
    public void approveOrder(long orderId) {
        updateOrder(orderId, Order::noteApproved);
        meterRegistry.ifPresent(mr -> mr.counter("approved_orders").increment());
    }

    // 주문 거절
    public void rejectOrder(long orderId) {
        updateOrder(orderId, Order::noteRejected);
        meterRegistry.ifPresent(mr -> mr.counter("rejected_orders").increment());
    }

    // 주문 취소
    public Order cancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        CancelOrderSagaData sagaData = new CancelOrderSagaData(order.getConsumerId(), orderId, order.getOrderTotal());
        cancelOrderSagaManager.create(sagaData);
        return order;
    }

    // 주문 취소
    public void beginCancel(long orderId) {
        updateOrder(orderId, Order::cancel);
    }

    // 주문 취소 언두
    public void undoCancel(long orderId) {
        updateOrder(orderId, Order::undoPendingCancel);
    }

    // 주문 취소 확인
    public void confirmCancelled(long orderId) {
        updateOrder(orderId, Order::noteCancelled);
    }

    // 주문 변경
    public Order reviseOrder(long orderId, OrderRevision orderRevision) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        ReviseOrderSagaData sagaData = new ReviseOrderSagaData(order.getConsumerId(), orderId, null, orderRevision);
        reviseOrderSagaManager.create(sagaData);
        return order;
    }

    // 주문 변경 확인
    public Optional<Order> confirmChangeLineItemQuantity(Long orderId, OrderRevision orderRevision) {
        return orderRepository.findById(orderId).map(order -> {
            List<OrderDomainEvent> events = order.confirmRevision(orderRevision);
            orderAggregateEventPublisher.publish(order, events);
            return order;
        });
    }

    // 주문 변경 시작
    public Optional<RevisedOrder> beginReviseOrder(long orderId, OrderRevision revision) {
        return orderRepository.findById(orderId).map(order -> {
            ResultWithDomainEvents<LineItemQuantityChange, OrderDomainEvent> result = order.revise(revision);
            orderAggregateEventPublisher.publish(order, result.events);
            return new RevisedOrder(order, result.result);
        });
    }

    // 주문 변경 언두
    public void undoPendingRevision(long orderId) {
        updateOrder(orderId, Order::rejectRevision);
    }

    // 주문 변경 확인
    public void confrimRevision(long orderId, OrderRevision revision) {
        updateOrder(orderId, order -> order.confirmRevision(revision));
    }

    // 지원안함?
    public void noteReversingAuthorization(Long orderId) {
        throw new UnsupportedOperationException();
    }

    // 레스토랑 추가?
    @Transactional(propagation = Propagation.MANDATORY)
    public void createMenu(long id, String name, RestaurantMenu menu) {
        Restaurant restaurant = new Restaurant(id, name, menu.getMenuItems());
        restaurantRepository.save(restaurant);
    }

    // 레스토랑 메뉴 변경?
    @Transactional(propagation = Propagation.MANDATORY)
    public void reviseMenu(long id, RestaurantMenu revisedMenu) {
        restaurantRepository.findById(id).map(restaurant -> {
            List<OrderDomainEvent> events = restaurant.reviseMenu(revisedMenu);
            return restaurant;
        }).orElseThrow(RuntimeException::new);
    }
}
