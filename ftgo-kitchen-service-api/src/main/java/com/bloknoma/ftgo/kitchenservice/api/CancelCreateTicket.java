package com.bloknoma.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

// 티켓 생성 취소 커맨드?
public class CancelCreateTicket implements Command {
    private Long ticketId;

    private CancelCreateTicket() {
    }

    public CancelCreateTicket(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}
