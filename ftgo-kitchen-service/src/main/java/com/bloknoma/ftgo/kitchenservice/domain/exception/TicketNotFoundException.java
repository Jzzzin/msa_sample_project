package com.bloknoma.ftgo.kitchenservice.domain.exception;

// 티켓 조회 실패 예외 처리
public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(long orderId) {
        super("Ticket not found: " + orderId);
    }
}
