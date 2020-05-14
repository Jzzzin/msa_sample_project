package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;

// 주문 수정 시작 커맨드?
public class BeginReviseOrderCommand extends OrderCommand {

    private OrderRevision revision;

    private BeginReviseOrderCommand() {
    }

    public BeginReviseOrderCommand(long orderId, OrderRevision revision) {
        super(orderId);
        this.revision = revision;
    }

    public OrderRevision getRevision() {
        return revision;
    }

    public void setRevision(OrderRevision revision) {
        this.revision = revision;
    }
}
