package com.bloknoma.ftgo.orderservice.domain.value;

import java.util.Map;
import java.util.Optional;

// 주문 변경 정보
public class OrderRevision {

    private Optional<DeliveryInformation> deliveryInformation = Optional.empty();
    private Map<String, Integer> revisedLineItemQuantities;

    private OrderRevision() {
    }

    public OrderRevision(Optional<DeliveryInformation> deliveryInformation, Map<String, Integer> revisedLineItemQuantities) {
        this.deliveryInformation = deliveryInformation;
        this.revisedLineItemQuantities = revisedLineItemQuantities;
    }

    public Optional<DeliveryInformation> getDeliveryInformation() {
        return deliveryInformation;
    }

    public void setDeliveryInformation(Optional<DeliveryInformation> deliveryInformation) {
        this.deliveryInformation = deliveryInformation;
    }

    public Map<String, Integer> getRevisedLineItemQuantities() {
        return revisedLineItemQuantities;
    }

    public void setRevisedLineItemQuantities(Map<String, Integer> revisedLineItemQuantities) {
        this.revisedLineItemQuantities = revisedLineItemQuantities;
    }
}
