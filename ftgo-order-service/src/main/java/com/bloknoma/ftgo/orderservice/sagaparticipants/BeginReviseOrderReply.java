package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.common.Money;

// 주문 수정 시작 응답?
public class BeginReviseOrderReply {
    private Money revisedOrderTotal;

    public BeginReviseOrderReply() {
    }

    public BeginReviseOrderReply(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }

    public Money getRevisedOrderTotal() {
        return revisedOrderTotal;
    }

    public void setRevisedOrderTotal(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }
}
