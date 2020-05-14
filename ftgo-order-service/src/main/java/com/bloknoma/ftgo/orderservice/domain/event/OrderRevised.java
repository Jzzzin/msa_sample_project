package com.bloknoma.ftgo.orderservice.domain.event;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.orderservice.api.events.OrderDomainEvent;
import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;

// 주문 수정 승인 이벤트
public class OrderRevised implements OrderDomainEvent {

    private final OrderRevision orderRevision;
    private final Money currentOrderTotal;
    private final Money newOrderTotal;

    public OrderRevised(OrderRevision orderRevision, Money currentOrderTotal, Money newOrderTotal) {
        this.orderRevision = orderRevision;
        this.currentOrderTotal = currentOrderTotal;
        this.newOrderTotal = newOrderTotal;
    }

    public OrderRevision getOrderRevision() {
        return orderRevision;
    }

    public Money getCurrentOrderTotal() {
        return currentOrderTotal;
    }

    public Money getNewOrderTotal() {
        return newOrderTotal;
    }
}
