package com.bloknoma.ftgo.apigateway.orders;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

// 주문 서비스, 주문 내역 서비스 URL 외부화 구성
@ConfigurationProperties(prefix = "order.destinations")
public class OrderDestinations {

    @NotNull
    private String orderServiceUrl;

    @NotNull
    private String orderHistoryServiceUrl;

    public String getOrderServiceUrl() {
        return orderServiceUrl;
    }

    public void setOrderServiceUrl(String orderServiceUrl) {
        this.orderServiceUrl = orderServiceUrl;
    }

    public String getOrderHistoryServiceUrl() {
        return orderHistoryServiceUrl;
    }

    public void setOrderHistoryServiceUrl(String orderHistoryServiceUrl) {
        this.orderHistoryServiceUrl = orderHistoryServiceUrl;
    }
}
