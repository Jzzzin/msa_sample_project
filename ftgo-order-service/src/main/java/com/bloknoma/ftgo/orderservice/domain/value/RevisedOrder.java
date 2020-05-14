package com.bloknoma.ftgo.orderservice.domain.value;

import com.bloknoma.ftgo.orderservice.domain.LineItemQuantityChange;
import com.bloknoma.ftgo.orderservice.domain.Order;

// 수정된 주문 정보
public class RevisedOrder {
    private final Order order;
    private final LineItemQuantityChange change;

    public RevisedOrder(Order order, LineItemQuantityChange change) {
        this.order = order;
        this.change = change;
    }

    public Order getOrder() {
        return order;
    }

    public LineItemQuantityChange getChange() {
        return change;
    }
}
