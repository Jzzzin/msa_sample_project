package com.bloknoma.ftgo.cqrs.orderhistory;

import io.eventuate.tram.events.common.DomainEvent;

// 주문 픽업 완료 이벤트
public class DeliveryPickedUp implements DomainEvent {
    private String orderId;

    public String getOrderId() {
        return orderId;
    }
}
