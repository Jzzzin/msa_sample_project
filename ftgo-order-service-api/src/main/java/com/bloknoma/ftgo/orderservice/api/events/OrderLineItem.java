package com.bloknoma.ftgo.orderservice.api.events;

import com.bloknoma.ftgo.common.Money;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

// 주문 목록 정보
@Embeddable
public class OrderLineItem {

    public OrderLineItem() {
    }

    private int quantity;
    private String menuItemId;
    private String name;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "amount", column = @Column(name = "price")))
    private Money price;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public OrderLineItem(String menuItemId, String name, Money price, int quantity) {
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
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

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    // 수량 변경 금액
    public Money deltaForChangedQuantity(int newQuantity) {
        return price.multiply(newQuantity - quantity);
    }

    // 주문 금액
    public Money getTotal() {
        return price.multiply(quantity);
    }

}
