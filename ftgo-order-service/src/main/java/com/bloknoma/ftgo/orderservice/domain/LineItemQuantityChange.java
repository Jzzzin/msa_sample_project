package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.common.Money;

// 주문 목록 수량 변경 금액 정보
public class LineItemQuantityChange {
    final Money currentOrderTotal;
    final Money newOrderTotal;
    final Money delta;

    public LineItemQuantityChange(Money currentOrderTotal, Money newOrderTotal, Money delta) {
        this.currentOrderTotal = currentOrderTotal;
        this.newOrderTotal = newOrderTotal;
        this.delta = delta;
    }

    public Money getCurrentOrderTotal() {
        return currentOrderTotal;
    }

    public Money getNewOrderTotal() {
        return newOrderTotal;
    }

    public Money getDelta() {
        return delta;
    }
}
