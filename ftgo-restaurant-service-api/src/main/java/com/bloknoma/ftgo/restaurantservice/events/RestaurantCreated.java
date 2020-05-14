package com.bloknoma.ftgo.restaurantservice.events;

import io.eventuate.tram.events.common.DomainEvent;

// 레스토랑 생성 이벤트?
public class RestaurantCreated implements DomainEvent {
    private String name;
    private RestaurantMenu menu;

    private RestaurantCreated() {
    }

    public RestaurantCreated(String name, RestaurantMenu menu) {
        this.name = name;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RestaurantMenu getMenu() {
        return menu;
    }

    public void setMenu(RestaurantMenu menu) {
        this.menu = menu;
    }
}
