package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.orderservice.api.events.OrderLineItem;
import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// 주문 목록 정보
@Embeddable
public class OrderLineItems {

    @ElementCollection
    @CollectionTable(name = "order_line_items")
    private List<OrderLineItem> lineItems;

    private OrderLineItems() {
    }

    public OrderLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public List<OrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    // 주문 목록 조회
    OrderLineItem findOrderLineItem(String lineItemId) {
        return lineItems.stream().filter(li -> li.getMenuItemId().equals(lineItemId)).findFirst().get();
    }

    // 주문 총액 변경 금액 구하기
    Money changeToOrderTotal(OrderRevision orderRevision) {
        AtomicReference<Money> delta = new AtomicReference<>(Money.ZERO);

        orderRevision.getRevisedLineItemQuantities().forEach((lineItemId, newQuantity) -> {
            OrderLineItem lineItem = findOrderLineItem(lineItemId);
            delta.set(delta.get().add(lineItem.deltaForChangedQuantity(newQuantity)));
        });
        return delta.get();
    }

    // 주문 수량 변경
    void updateLineItems(OrderRevision orderRevision) {
        getLineItems().stream().forEach(li -> {
            Integer revised = orderRevision.getRevisedLineItemQuantities().get(li.getMenuItemId());
            li.setQuantity(revised);
        });
    }

    // 주문 총액 조회
    Money orderTotal() {
        return lineItems.stream().map(OrderLineItem::getTotal).reduce(Money.ZERO, Money::add);
    }

    // 주문 목록 수량 변경 정보 구하기
    LineItemQuantityChange lineItemQuantityChange(OrderRevision orderRevision) {
        Money currentOrderTotal = orderTotal();
        Money delta = changeToOrderTotal(orderRevision);
        Money newOrderTotal = currentOrderTotal.add(delta);
        return new LineItemQuantityChange(currentOrderTotal, newOrderTotal, delta);
    }
}
