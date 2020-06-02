package com.bloknoma.ftgo.cqrs.orderhistory.messaging;

import com.bloknoma.ftgo.cqrs.orderhistory.DeliveryPickedUp;
import com.bloknoma.ftgo.cqrs.orderhistory.Location;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryDao;
import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.Order;
import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.SourceEvent;
import com.bloknoma.ftgo.orderservice.api.events.*;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

// 이벤트 핸들러
public class OrderHistoryEventHandlers {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private OrderHistoryDao orderHistoryDao;

    public OrderHistoryEventHandlers(OrderHistoryDao orderHistoryDao) {
        this.orderHistoryDao = orderHistoryDao;
    }

    // TODO - determine events

    private String orderId;
    private Order order;
    private Location location; //

    // 이벤트 핸들러 라우팅
    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("com.bloknoma.ftgo.orderservice.domain.Order")
                .onEvent(OrderCreatedEvent.class, this::handleOrderCreated)
                .onEvent(OrderAuthorized.class, this::handleOrderAuthorized)
                .onEvent(OrderCancelled.class, this::handleOrderCancelled)
                .onEvent(OrderRejected.class, this::handleOrderRejected)
//                .onEvent(DeliveryPickedUp.class, this::handleDeliveryPickedUp)
                .build();
    }

    // 이벤트 생성
    private Optional<SourceEvent> makeSourceEvent(DomainEventEnvelope<?> dee) {
        return Optional.of(new SourceEvent(dee.getAggregateType(), dee.getAggregateId(), dee.getEventId()));
    }

    // 주문 생성
    public void handleOrderCreated(DomainEventEnvelope<OrderCreatedEvent> dee) {
        logger.debug("handleOrderCreated called {}", dee);
        boolean result = orderHistoryDao.addOrder(makeOrder(dee.getAggregateId(), dee.getEvent()), makeSourceEvent(dee));
        logger.debug("handleOrderCreated result {} {}", dee, result);
    }

    private Order makeOrder(String orderId, OrderCreatedEvent event) {
        return new Order(orderId,
                Long.toString(event.getOrderDetails().getConsumerId()),
                OrderState.APPROVAL_PENDING,
                event.getOrderDetails().getLineItems(),
                event.getOrderDetails().getOrderTotal(),
                event.getOrderDetails().getRestaurantId(),
                event.getRestaurantName());
    }

    // 주문 승인
    public void handleOrderAuthorized(DomainEventEnvelope<OrderAuthorized> dee) {
        logger.debug("handleOrderAuthorized called {}", dee);
        boolean result = orderHistoryDao.updateOrderState(dee.getAggregateId(), OrderState.APPROVED, makeSourceEvent(dee));
        logger.debug("handleOrderAuthorized result {} {}", dee, result);
    }

    // 주문 취소
    public void handleOrderCancelled(DomainEventEnvelope<OrderCancelled> dee) {
        logger.debug("handleOrderCancelled called {}", dee);
        boolean result = orderHistoryDao.updateOrderState(dee.getAggregateId(), OrderState.CANCELLED, makeSourceEvent(dee));
        logger.debug("handleOrderCancelled result {} {}", dee, result);
    }

    // 주문 거절
    public void handleOrderRejected(DomainEventEnvelope<OrderRejected> dee) {
        logger.debug("handleOrderRejected called {}", dee);
        boolean result = orderHistoryDao.updateOrderState(dee.getAggregateId(), OrderState.REJECTED, makeSourceEvent(dee));
        logger.debug("handleOrderRejected result {} {}", dee, result);
    }

    // 배달 픽업
    public void handleDeliveryPickedUp(DomainEventEnvelope<DeliveryPickedUp> dee) {
        orderHistoryDao.notePickedUp(dee.getEvent().getOrderId(), makeSourceEvent(dee));
    }

    // TODO - need a common API that abstracts message vs. event sourcing

/*
    public void handleOrderCancelled() {
        orderHistoryDao.cancelOrder(orderId, null);
    }

    public void handleTicketPreparationStarted() {
        orderHistoryDao.noteTicketPreparationStarted(orderId);
    }

    public void handleTicketPreparationCompleted() {
        orderHistoryDao.noteTicketPreparationCompleted(orderId);
    }

    public void handleDeliveryLocationUpdated() {
        orderHistoryDao.updateLocation(orderId, location);
    }

    public void handleDeliveryDelivered() {
        orderHistoryDao.noteDelivered(orderId);
    }
*/
}
