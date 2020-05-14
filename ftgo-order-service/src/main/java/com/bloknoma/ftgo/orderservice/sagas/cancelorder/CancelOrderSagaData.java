package com.bloknoma.ftgo.orderservice.sagas.cancelorder;

import com.bloknoma.ftgo.common.Money;

// 주문 취소 사가 정보
public class CancelOrderSagaData {

    private long consumerId;
    private Long orderId;
    private Money orderTotal;

    private String reverseRequestId;
    private long restaurantId;

    private CancelOrderSagaData() {
    }

    public CancelOrderSagaData(long consumerId, Long orderId, Money orderTotal) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public String getReverseRequestId() {
        return reverseRequestId;
    }

    public void setReverseRequestId(String reverseRequestId) {
        this.reverseRequestId = reverseRequestId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
