package com.bloknoma.ftgo.cqrs.orderhistory;

import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.Order;

import java.util.List;
import java.util.Optional;

// 주문 내역
public class OrderHistory {

    private List<Order> orders;
    private Optional<String> startKey;

    public OrderHistory(List<Order> orders, Optional<String> startKey) {
        this.orders = orders;
        this.startKey = startKey;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Optional<String> getStartKey() {
        return startKey;
    }
}
