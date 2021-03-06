package com.bloknoma.ftgo.orderservice.api.web;

import java.util.Map;

// 주문 수정 요청?
public class ReviseOrderRequest {
    private Map<String, Integer> revisedLineItemQuantities;

    private ReviseOrderRequest() {
    }

    public ReviseOrderRequest(Map<String, Integer> revisedLineItemQuantities) {
        this.revisedLineItemQuantities = revisedLineItemQuantities;
    }

    public Map<String, Integer> getRevisedLineItemQuantities() {
        return revisedLineItemQuantities;
    }

    public void setRevisedLineItemQuantities(Map<String, Integer> revisedLineItemQuantities) {
        this.revisedLineItemQuantities = revisedLineItemQuantities;
    }
}
