package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.orderservice.api.events.OrderDomainEvent;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import org.junit.Before;

public class OrderTest {

    private ResultWithDomainEvents<Order, OrderDomainEvent> createResult;
    private Order order;

    @Before
    public void setUp() throws Exception {
        createResult = Order.createOrder(CONSUMER_ID)
    }
}

