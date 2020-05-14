package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.orderservice.RestaurantMother;
import com.bloknoma.ftgo.orderservice.api.events.OrderCreatedEvent;
import com.bloknoma.ftgo.orderservice.domain.repository.OrderRepository;
import com.bloknoma.ftgo.orderservice.domain.repository.RestaurantRepository;
import com.bloknoma.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import com.bloknoma.ftgo.orderservice.sagas.createorder.CreateOrderSagaState;
import com.bloknoma.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.*;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    private OrderService orderService;

    private OrderRepository orderRepository;
    private DomainEventPublisher eventPublisher;
    private RestaurantRepository restaurantRepository;
    private SagaManager<CreateOrderSagaState> createOrderSagaManager;
    private SagaManager<CancelOrderSagaData> cancelOrderSagaManager;
    private SagaManager<ReviseOrderSagaData> reviseOrderSagaManager;
    private OrderDomainEventPublisher orderAggregateEventPublisher;

    // 목 설정 및 서비스 구성
    @Before
    public void setup() {
        orderRepository = mock(OrderRepository.class);
        eventPublisher = mock(DomainEventPublisher.class);
        restaurantRepository = mock(RestaurantRepository.class);
        createOrderSagaManager = mock(SagaManager.class);
        cancelOrderSagaManager = mock(SagaManager.class);
        reviseOrderSagaManager = mock(SagaManager.class);

        // Mock DomainEventPublisher AND use the real OrderDomainEventPublisher
        orderAggregateEventPublisher = mock(OrderDomainEventPublisher.class);

        orderService = new OrderService(orderRepository, eventPublisher, restaurantRepository,
                createOrderSagaManager, cancelOrderSagaManager, reviseOrderSagaManager, orderAggregateEventPublisher, Optional.empty());
    }

    // 주문 생성
    @Test
    public void shouldCreateOrder() {
        // 레스토랑 조회 목 설정
        when(restaurantRepository.findById(AJANTA_ID)).thenReturn(Optional.of(AJANTA_RESTAURANT));
        // 주문 저장 목 설정
        when(orderRepository.save(any(Order.class))).then(invocation -> {
            Order order = (Order) invocation.getArguments()[0];
            order.setId(ORDER_ID);
            return order;
        });

        // 주문 생성 처리
        Order order = orderService.createOrder(CONSUMER_ID, AJANTA_ID, CHICKEN_VINDALOO_MENU_ITEMS_AND_QUANTITIES);

        // 주문 저장 확인
        verify(orderRepository).save(same(order));

        // 주문 생성 이벤트 확인
        verify(orderAggregateEventPublisher).publish(order,
                Collections.singletonList(new OrderCreatedEvent(CHICKEN_VINDALOO_ORDER_DETAILS, RestaurantMother.AJANTA_RESTAURANT_NAME)));

        // 주문 생성 사가 확인
        verify(createOrderSagaManager).create(new CreateOrderSagaState(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS), Order.class, ORDER_ID);
    }

    //  TODO write tests for other methods
}
