package com.bloknoma.ftgo.orderservice.domain.event;

import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import io.eventuate.tram.events.common.DomainEvent;

// 주문 취소 요청 ?
public class OrderCancelRequested implements DomainEvent {
    private OrderState state;

    public OrderCancelRequested(OrderState state) {
        this.state = state;
    }

    public OrderState getState() {
        return state;
    }
}
