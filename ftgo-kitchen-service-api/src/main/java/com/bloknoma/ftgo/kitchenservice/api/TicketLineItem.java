package com.bloknoma.ftgo.kitchenservice.api;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

// 티켓 목록 정보
@Embeddable
@Access(AccessType.FIELD)
public class TicketLineItem {

    private String menuItemId;
    private String name;
    private int quantity;

    private TicketLineItem() {

    }

    public TicketLineItem(String menuItemId, String name, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(String menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
