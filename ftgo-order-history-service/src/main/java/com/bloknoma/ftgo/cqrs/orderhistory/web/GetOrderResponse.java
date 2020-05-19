package com.bloknoma.ftgo.cqrs.orderhistory.web;

import com.bloknoma.ftgo.orderservice.api.events.OrderState;

// 주문 조회 응답
public class GetOrderResponse {
    private String orderId;
    private OrderState status;
    private long restaurantId;
    private String restaurantName;

    private GetOrderResponse() {
    }

    public GetOrderResponse(String orderId, OrderState status, long restaurantId, String restaurantName) {
        this.orderId = orderId;
        this.status = status;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public OrderState getStatus() {
        return status;
    }

    public void setStatus(OrderState status) {
        this.status = status;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
