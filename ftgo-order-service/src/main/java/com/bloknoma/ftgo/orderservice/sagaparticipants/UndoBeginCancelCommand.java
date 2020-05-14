package com.bloknoma.ftgo.orderservice.sagaparticipants;

// 주문 취소 시작 커맨드 언두
public class UndoBeginCancelCommand extends OrderCommand {
    public UndoBeginCancelCommand(long orderId) {
        super(orderId);
    }
}
