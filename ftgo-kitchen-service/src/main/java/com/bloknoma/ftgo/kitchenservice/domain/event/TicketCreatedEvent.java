package com.bloknoma.ftgo.kitchenservice.domain.event;

import com.bloknoma.ftgo.kitchenservice.api.TicketDetails;

// 티켓 생성 이벤트
public class TicketCreatedEvent implements TicketDomainEvent {
    public TicketCreatedEvent(Long id, TicketDetails details) {

    }
}
