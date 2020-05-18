package com.bloknoma.ftgo.restaurantservice.web;

// 레스토랑 조회 응답
public class GetRestaurantResponse {
    private Long id;

    private String name;

    public GetRestaurantResponse() {
    }

    public GetRestaurantResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
