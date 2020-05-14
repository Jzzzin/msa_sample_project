package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.common.UnsupportedStateTransitionException;
import com.bloknoma.ftgo.orderservice.api.events.*;
import com.bloknoma.ftgo.orderservice.domain.value.DeliveryInformation;
import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;
import com.bloknoma.ftgo.orderservice.domain.value.PaymentInformation;
import com.bloknoma.ftgo.orderservice.domain.event.*;
import com.bloknoma.ftgo.orderservice.domain.exception.OrderMinimumNotMetException;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;

import javax.persistence.*;
import java.util.List;

import static com.bloknoma.ftgo.orderservice.api.events.OrderState.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

// 주문 정보
@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private Long consumerId;
    private Long restaurantId;

    @Embedded
    private OrderLineItems orderLineItems;

    @Embedded
    private DeliveryInformation deliveryInformation;

    @Embedded
    private PaymentInformation paymentInformation;

    @Embedded
    private Money orderMinmum = new Money(Integer.MAX_VALUE);

    private Order() {
    }

    public Order(Long consumerId, Long restaurantId, List<OrderLineItem> orderLineItems) {
        this.consumerId = consumerId;
        this.restaurantId = restaurantId;
        this.orderLineItems = new OrderLineItems(orderLineItems);
        this.state = APPROVAL_PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public OrderState getState() {
        return state;
    }

    public Long getConsumerId() {
        return consumerId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    // 주문 목록 조회
    public List<OrderLineItem> getLineItems() {
        return orderLineItems.getLineItems();
    }

    // 주문 총액 조회
    public Money getOrderTotal() {
        return orderLineItems.orderTotal();
    }


    // 주문 생성
    public static ResultWithDomainEvents<Order, OrderDomainEvent> createOrder(long consumerId, Restaurant restaurant, List<OrderLineItem> orderLineItems) {
        Order order = new Order(consumerId, restaurant.getId(), orderLineItems);
        List<OrderDomainEvent> events = singletonList(new OrderCreatedEvent(
                new OrderDetails(consumerId, restaurant.getId(), orderLineItems, order.getOrderTotal()), restaurant.getName()
        ));
        return new ResultWithDomainEvents<>(order, events);
    }

    // 주문 취소
    public List<OrderDomainEvent> cancel() {
        switch (state) {
            case APPROVED:
                this.state = OrderState.CANCEL_PENDING;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 주문 취소 undo
    public List<OrderDomainEvent> undoPendingCancel() {
        switch (state) {
            case CANCEL_PENDING:
                this.state = OrderState.APPROVED;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 주문 취소 완료
    public List<OrderDomainEvent> noteCancelled() {
        switch (state) {
            case CANCEL_PENDING:
                this.state = OrderState.CANCELLED;
                return singletonList(new OrderCancelled());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 주문 승인 완료
    public List<OrderDomainEvent> noteApproved() {
        switch (state) {
            case APPROVAL_PENDING:
                this.state = OrderState.APPROVED;
                return singletonList(new OrderAuthorized());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 주문 거절 완료
    public List<OrderDomainEvent> noteRejected() {
        switch (state) {
            case APPROVAL_PENDING:
                this.state = OrderState.REJECTED;
                return singletonList(new OrderRejected());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    //
    public List<OrderDomainEvent> noteReversingAuthorization() {
        return null;
    }

    // 주문 수정
    public ResultWithDomainEvents<LineItemQuantityChange, OrderDomainEvent> revise(OrderRevision orderRevision) {
        switch (state) {
            case APPROVED:
                // 주문 한도 체크?
                LineItemQuantityChange change = orderLineItems.lineItemQuantityChange(orderRevision);
                if (change.newOrderTotal.isGreaterThanOrEqual(orderMinmum)) {
                    throw new OrderMinimumNotMetException();
                }
                this.state = OrderState.REVISION_PENDING;
                return new ResultWithDomainEvents<>(change, singletonList(new OrderRevisionProposed(orderRevision, change.currentOrderTotal, change.newOrderTotal)));
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 주문 수정 거절
    public List<OrderDomainEvent> rejectRevision() {
        switch (state) {
            case REVISION_PENDING:
                this.state = OrderState.APPROVED;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 주문 수정 승인
    public List<OrderDomainEvent> confirmRevision(OrderRevision orderRevision) {
        switch (state) {
            case REVISION_PENDING:
                LineItemQuantityChange licd = orderLineItems.lineItemQuantityChange(orderRevision);

                // 배달 정보 변경
                orderRevision.getDeliveryInformation().ifPresent(newDi -> this.deliveryInformation = newDi);

                // 주문 목록 수량 변경
                if (!orderRevision.getRevisedLineItemQuantities().isEmpty()) {
                    orderLineItems.updateLineItems(orderRevision);
                }

                this.state = OrderState.APPROVED;
                return singletonList(new OrderRevised(orderRevision, licd.currentOrderTotal, licd.newOrderTotal));
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

}
