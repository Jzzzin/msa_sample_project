package com.bloknoma.ftgo.cqrs.orderhistory;

import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.Order;
import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.SourceEvent;
import com.bloknoma.ftgo.orderservice.api.events.OrderState;

import java.util.Optional;

public interface OrderHistoryDao {

    OrderHistory findOrderHistory(String consumerId, OrderHistoryFilter filter);

    boolean addOrder(Order order, Optional<SourceEvent> eventSource);

    boolean updateOrderState(String orderId, OrderState newState, Optional<SourceEvent> eventSource);

    void noteTicketPreparationStarted(String orderId);

    void noteTicketPreparationCompleted(String orderId);

    void notePickedUp(String orderId, Optional<SourceEvent> eventSource);

    void updateLocation(String orderId, Location location);

    void noteDelivered(String orderId);

    Optional<Order> findOrder(String orderId);
}
