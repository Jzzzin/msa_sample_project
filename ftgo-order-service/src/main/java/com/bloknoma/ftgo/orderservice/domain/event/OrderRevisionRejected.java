package com.bloknoma.ftgo.orderservice.domain.event;

import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;
import io.eventuate.tram.events.common.DomainEvent;

// 주문 수정 거절?
public class OrderRevisionRejected implements DomainEvent {
    public OrderRevisionRejected(OrderRevision orderRevision) {
        throw new UnsupportedOperationException();
    }
}
