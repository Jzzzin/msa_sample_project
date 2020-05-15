package com.bloknoma.ftgo.kitchenservice.domain;

import com.bloknoma.ftgo.kitchenservice.api.TicketDetails;
import com.bloknoma.ftgo.restaurantservice.events.MenuItem;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;
import io.eventuate.tram.events.common.DomainEvent;

import javax.persistence.*;
import java.util.List;

// 레스토랑 정보
@Entity
@Table(name = "kitchen_service_restaurants")
@Access(AccessType.FIELD)
public class Restaurant {

    @Id
    private Long id;

    @Embedded
    @ElementCollection
    @CollectionTable(name = "kitchen_service_restaurant_menu_items")
    private List<MenuItem> menuItems;

    private Restaurant() {
    }

    public Restaurant(Long id, List<MenuItem> menuItems) {
        this.id = id;
        this.menuItems = menuItems;
    }

    public Long getId() {
        return id;
    }

    // 메뉴 수정 - 지원 안함
    public List<DomainEvent> reviseMenu(RestaurantMenu revisedMenu) {
        throw new UnsupportedOperationException();
    }

    // 레스토랑 검증 - 미구현
    public void verifyRestaurantDetails(TicketDetails ticketDetails) {
        // TODO - implement me
    }

}
