package com.bloknoma.ftgo.kitchenservice.web;

// 레스토랑 응답
public class GetRestaurantResponse {
    private long restaurantId;

    public GetRestaurantResponse() {

    }

    public GetRestaurantResponse(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
