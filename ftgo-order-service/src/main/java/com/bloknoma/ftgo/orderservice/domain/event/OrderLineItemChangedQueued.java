package com.bloknoma.ftgo.orderservice.domain.event;

import io.eventuate.tram.events.common.DomainEvent;

// 주문 목록 변경?
public class OrderLineItemChangedQueued implements DomainEvent {
    public OrderLineItemChangedQueued(String lineItemId, int newQuantity) {

    }
}
