package com.bloknoma.ftgo.restaurantservice.lambda;

// restaurant response pojo
public class GetRestaurantResponse {
    private String name;

    public GetRestaurantResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
