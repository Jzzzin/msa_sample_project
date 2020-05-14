package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.common.Money;

// 주문 수정 시작 응답?
public class BeginRiviseOrderReply {
    private Money revisedOrderTotal;

    public BeginRiviseOrderReply() {
    }

    public BeginRiviseOrderReply(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }

    public Money getRevisedOrderTotal() {
        return revisedOrderTotal;
    }

    public void setRevisedOrderTotal(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }
}
