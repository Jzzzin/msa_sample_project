package com.bloknoma.ftgo.consumerservice.api.web;

// 고객 생성 응답
public class CreateConsumerResponse {
    private long consumerId;

    public CreateConsumerResponse() {

    }

    public CreateConsumerResponse(long consumerId) {
        this.consumerId = consumerId;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }
}
