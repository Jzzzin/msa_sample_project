package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;

// 주문 수정 커맨드 승인?
public class ConfirmReviseOrderCommand extends OrderCommand {

    private OrderRevision revision;

    private ConfirmReviseOrderCommand() {
    }

    public ConfirmReviseOrderCommand(long orderId, OrderRevision revision) {
        super(orderId);
        this.revision = revision;
    }

    public OrderRevision getRevision() {
        return revision;
    }
}
