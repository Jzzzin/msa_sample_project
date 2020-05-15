package com.bloknoma.ftgo.kitchenservice.domain.event;

import java.time.LocalDateTime;

// 티켓 승인 이벤트
public class TicketAcceptedEvent implements TicketDomainEvent {
    public TicketAcceptedEvent(LocalDateTime readyBy) {

    }
}
