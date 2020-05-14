package com.bloknoma.ftgo.orderservice.sagas.reviseorder;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;

public class ReviseOrderSagaData {

    private long consumerId;
    private Long orderId;
    private Long expectedVersion;
    private OrderRevision orderRevision;
    private long restaurantId;
    private Money revisedOrderTotal;

    private ReviseOrderSagaData() {
    }

    public ReviseOrderSagaData(long consumerId, Long orderId, Long expectedVersion, OrderRevision orderRevision) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.expectedVersion = expectedVersion;
        this.orderRevision = orderRevision;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getExpectedVersion() {
        return expectedVersion;
    }

    public void setExpectedVersion(Long expectedVersion) {
        this.expectedVersion = expectedVersion;
    }

    public OrderRevision getOrderRevision() {
        return orderRevision;
    }

    public void setOrderRevision(OrderRevision orderRevision) {
        this.orderRevision = orderRevision;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Money getRevisedOrderTotal() {
        return revisedOrderTotal;
    }

    public void setRevisedOrderTotal(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }
}
