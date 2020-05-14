package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.kitchenservice.api.TicketDetails;
import com.bloknoma.ftgo.orderservice.api.events.OrderDomainEvent;
import com.bloknoma.ftgo.restaurantservice.events.MenuItem;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

// 레스토랑 정보
@Entity
@Table(name = "order_service_restaurants")
@Access(AccessType.FIELD)
public class Restaurant {

    @Id
    private Long id;

    private String name;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "order_service_restaurant_menu_items")
    private List<MenuItem> menuItems;

    private Restaurant() {
    }

    public Restaurant(Long id, String name, List<MenuItem> menuItems) {
        this.id = id;
        this.name = name;
        this.menuItems = menuItems;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    // 메뉴 수정 - 지원 안함
    public List<OrderDomainEvent> reviseMenu(RestaurantMenu revisedMenu) {
        throw new UnsupportedOperationException();
    }

    // 레스토랑 정보 확인
    public void verifyRestaurantDetails(TicketDetails ticketDetails) {
        // TODO - implement me
    }

    // 메뉴 조회
    public Optional<MenuItem> findMenuItem(String menuItemId) {
        return menuItems.stream().filter(mi -> mi.getId().equals(menuItemId)).findFirst();
    }

}
