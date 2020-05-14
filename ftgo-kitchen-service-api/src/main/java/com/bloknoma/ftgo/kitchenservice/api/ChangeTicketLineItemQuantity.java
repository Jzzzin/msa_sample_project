package com.bloknoma.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

// 티켓 목록 수량 변경 커맨드?
public class ChangeTicketLineItemQuantity implements Command {
    public ChangeTicketLineItemQuantity(Long orderId) {
    }
}
