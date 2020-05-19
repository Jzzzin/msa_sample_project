package com.bloknoma.ftgo.cqrs.orderhistory.dynamodb;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.orderservice.api.events.OrderLineItem;
import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import org.joda.time.DateTime;

import java.util.List;

// 주문 정보
public class Order {
    private String orderId;
    private String consumerId;
    private OrderState status;
    private List<OrderLineItem> lineItems;
    private Money orderTotal;
    private long restaurantId;
    private String restaurantName;
    private DateTime creationDate = DateTime.now();

    public Order(String orderId, String consumerId, OrderState status, List<OrderLineItem> lineItems, Money orderTotal, long restaurantId, String restaurantName) {
        this.orderId = orderId;
        this.consumerId = consumerId;
        this.status = status;
        this.lineItems = lineItems;
        this.orderTotal = orderTotal;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public OrderState getStatus() {
        return status;
    }

    public List<OrderLineItem> getLineItems() {
        return lineItems;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }
}
