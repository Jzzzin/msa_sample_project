package com.bloknoma.ftgo.orderservice.web;

import com.bloknoma.ftgo.common.Money;

// order 응답
public class GetOrderResponse {

    private long orderId;
    private String state;
    private Money orderTotal;

    private GetOrderResponse() {
    }

    public GetOrderResponse(long orderId, String state, Money orderTotal) {
        this.orderId = orderId;
        this.state = state;
        this.orderTotal = orderTotal;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
        this.orderTotal = orderTotal;
    }
}
