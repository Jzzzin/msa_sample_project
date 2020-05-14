package com.bloknoma.ftgo.orderservice.web;

// restaurant 응답
public class GetRestaurantResponse {

    private long restaurantId;

    private GetRestaurantResponse() {
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
