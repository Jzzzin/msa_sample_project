package com.bloknoma.ftgo.orderservice.api.web;

// 주문 생성 응답?
public class CreateOrderResponse {
    private long orderId;

    private CreateOrderResponse() {
    }

    public CreateOrderResponse(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
