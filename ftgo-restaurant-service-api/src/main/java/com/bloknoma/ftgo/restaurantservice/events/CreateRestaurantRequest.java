package com.bloknoma.ftgo.restaurantservice.events;

// 레스토랑 생성 요청?
public class CreateRestaurantRequest {

    private String name;
    private RestaurantMenu menu;

    private CreateRestaurantRequest() {

    }

    public CreateRestaurantRequest(String name, RestaurantMenu menu) {
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
