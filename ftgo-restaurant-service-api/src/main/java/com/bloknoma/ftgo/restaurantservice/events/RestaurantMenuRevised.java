package com.bloknoma.ftgo.restaurantservice.events;

import io.eventuate.tram.events.common.DomainEvent;

// 레스토랑 메뉴 수정 이벤트?
public class RestaurantMenuRevised implements DomainEvent {

    private RestaurantMenu menu;

    public RestaurantMenu getRevisedMenu() {
        return menu;
    }
}
