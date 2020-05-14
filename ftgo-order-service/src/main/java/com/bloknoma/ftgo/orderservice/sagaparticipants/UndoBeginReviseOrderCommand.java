package com.bloknoma.ftgo.orderservice.sagaparticipants;

// 주문 수정 시작 커맨드 언두
public class UndoBeginReviseOrderCommand extends OrderCommand {

    protected UndoBeginReviseOrderCommand() {
    }

    public UndoBeginReviseOrderCommand(long orderId) {
        super(orderId);
    }
}
