package com.bloknoma.ftgo.orderservice.sagaparticipants;

// 주문 취소 시작 커맨드?
public class BeginCancelCommand extends OrderCommand {

    private BeginCancelCommand() {
    }

    public BeginCancelCommand(long orderId) {
        super(orderId);
    }
}
