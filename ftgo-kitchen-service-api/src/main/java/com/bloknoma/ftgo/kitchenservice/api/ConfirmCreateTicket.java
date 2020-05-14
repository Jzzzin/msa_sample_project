package com.bloknoma.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

// 티켓 생성 승인 커맨드?
public class ConfirmCreateTicket implements Command {
    private Long ticketId;

    private ConfirmCreateTicket() {
    }

    public ConfirmCreateTicket(Long ticketId) {
        this.ticketId = ticketId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}
