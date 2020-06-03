package com.bloknoma.ftgo.orderservice.messaging;

import com.bloknoma.ftgo.orderservice.domain.OrderService;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantCreated;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenuRevised;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 이벤트 메시지 컨슈머
public class OrderEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private OrderService orderService;

    public OrderEventConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    // 이벤트 핸들러 등록
    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("com.bloknoma.ftgo.restaurantservice.domain.Restaurant")
                .onEvent(RestaurantCreated.class, this::createMenu)
                .onEvent(RestaurantMenuRevised.class, this::reviseMenu)
                .build();
    }

    // 메뉴 추가 이벤트
    private void createMenu(DomainEventEnvelope<RestaurantCreated> de) {
        String restaurantIds = de.getAggregateId();
        long id = Long.parseLong(restaurantIds);
        RestaurantMenu menu = de.getEvent().getMenu();
        orderService.createMenu(id, de.getEvent().getName(), menu);
    }

    // 메뉴 변경 이벤트
    public void reviseMenu(DomainEventEnvelope<RestaurantMenuRevised> de) {
        String restaurantIds = de.getAggregateId();
        long id = Long.parseLong(restaurantIds);
        RestaurantMenu revisedMenu = de.getEvent().getRevisedMenu();

        orderService.reviseMenu(id, revisedMenu);
    }
}
