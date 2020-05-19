package com.bloknoma.ftgo.cqrs.orderhistory.web;

import java.util.List;

// 주문 내역 조회 응답
public class GetOrdersResponse {
    private List<GetOrderResponse> orders;
    private String startKey;

    private GetOrdersResponse() {
    }

    public GetOrdersResponse(List<GetOrderResponse> orders, String startKey) {
        this.orders = orders;
        this.startKey = startKey;
    }

    public List<GetOrderResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<GetOrderResponse> orders) {
        this.orders = orders;
    }

    public String getStartKey() {
        return startKey;
    }

    public void setStartKey(String startKey) {
        this.startKey = startKey;
    }
}
