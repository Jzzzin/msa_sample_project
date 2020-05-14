package com.bloknoma.ftgo.orderservice.web;

// 주문 목록 정보
public class MenuItemIdAndQuantity {

    private String menuItemId;
    private int quantity;

    public MenuItemIdAndQuantity(String menuItemId, int quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }
}
