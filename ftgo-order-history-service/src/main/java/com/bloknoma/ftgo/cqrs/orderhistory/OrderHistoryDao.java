package com.bloknoma.ftgo.cqrs.orderhistory;

import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.Order;
import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.SourceEvent;

import java.util.Optional;

public interface OrderHistoryDao {

    OrderHistory findOrderHistory(String consumerId, OrderHistoryFilter filter);

    boolean addOrder(Order order, Optional<SourceEvent> eventSource);

    public boolean cancelOrder(String orderId, Optional<SourceEvent> eventSource);

    Optional<Order> findOrder(String orderId);

    void noteTicketPreparationStarted(String orderId);

    void noteTicketPreparationCompleted(String orderId);

    void notePickedUp(String orderId, Optional<SourceEvent> eventSource);

    void updateLocation(String orderId, Location location);

    void noteDelivered(String orderId);
}
