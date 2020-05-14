package com.bloknoma.ftgo.orderservice;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.orderservice.api.events.OrderDetails;
import com.bloknoma.ftgo.orderservice.api.events.OrderLineItem;
import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import com.bloknoma.ftgo.orderservice.domain.Order;
import com.bloknoma.ftgo.orderservice.web.MenuItemIdAndQuantity;

import java.util.Collections;
import java.util.List;

import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.CHICKEN_VINDALOO;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.CHICKEN_VINDALOO_PRICE;

public class OrderDetailsMother {

    public static long CONSUMER_ID = 1511300065921L;

    public static final int CHICKEN_VINDALOO_QUANTITY = 5;
    public static final MenuItemIdAndQuantity CHICKEN_VINDALOO_MENU_ITEM_AND_QUANTITY = new MenuItemIdAndQuantity(RestaurantMother.CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO_QUANTITY);
    public static final List<MenuItemIdAndQuantity> CHICKEN_VINDALOO_MENU_ITEMS_AND_QUANTITIES = Collections.singletonList(CHICKEN_VINDALOO_MENU_ITEM_AND_QUANTITY);

    public static List<OrderLineItem> chickenVindalooLineItems() {
        return Collections.singletonList(new OrderLineItem(CHICKEN_VINDALOO_MENU_ITEM_AND_QUANTITY.getMenuItemId(),
                CHICKEN_VINDALOO,
                CHICKEN_VINDALOO_PRICE,
                CHICKEN_VINDALOO_MENU_ITEM_AND_QUANTITY.getQuantity()));
    }

    public static final Money CHICKEN_VINDALOO_ORDER_TOTAL = CHICKEN_VINDALOO_PRICE.multiply(5);
    public static final OrderDetails CHICKEN_VINDALOO_ORDER_DETAILS = new OrderDetails(CONSUMER_ID, AJANTA_ID,
            chickenVindalooLineItems(), CHICKEN_VINDALOO_ORDER_TOTAL);

    public static long ORDER_ID = 99L;

    public static Order CHICKEN_VINDALOO_ORDER = makeAjantaOrder();

    public static final OrderState CHICKEN_VINDALOO_ORDER_STATE = OrderState.APPROVAL_PENDING;

    private static Order makeAjantaOrder() {
        Order order = new Order(CONSUMER_ID, AJANTA_ID, chickenVindalooLineItems());
        order.setId(ORDER_ID);
        return order;
    }
}
