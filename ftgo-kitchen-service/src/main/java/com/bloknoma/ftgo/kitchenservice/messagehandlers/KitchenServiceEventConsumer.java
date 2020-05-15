package com.bloknoma.ftgo.kitchenservice.messagehandlers;

import com.bloknoma.ftgo.kitchenservice.domain.KitchenService;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantCreated;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenuRevised;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

// 이벤트 메시지 컨슈머
public class KitchenServiceEventConsumer {

    @Autowired
    private KitchenService kitchenService;

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
        kitchenService.createMenu(id, menu);
    }

    // 메뉴 변경 이벤트
    private void reviseMenu(DomainEventEnvelope<RestaurantMenuRevised> de) {
        long id = Long.parseLong(de.getAggregateId());
        RestaurantMenu revisedMenu = de.getEvent().getRevisedMenu();
        kitchenService.reviseMenu(id, revisedMenu);
    }
}

