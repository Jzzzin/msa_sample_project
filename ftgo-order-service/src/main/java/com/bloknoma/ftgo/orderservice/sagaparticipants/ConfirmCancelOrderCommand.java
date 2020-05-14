package com.bloknoma.ftgo.orderservice.sagaparticipants;

// 주문 취소 커맨드 승인?
public class ConfirmCancelOrderCommand extends OrderCommand {

    private ConfirmCancelOrderCommand() {
    }

    public ConfirmCancelOrderCommand(long orderId) {
        super(orderId);
    }
}
