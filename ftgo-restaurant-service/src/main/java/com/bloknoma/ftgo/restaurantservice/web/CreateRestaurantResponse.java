package com.bloknoma.ftgo.restaurantservice.web;

// 레스토랑 추가 응답
public class CreateRestaurantResponse {
    private long id;

    public CreateRestaurantResponse() {
    }

    public CreateRestaurantResponse(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
