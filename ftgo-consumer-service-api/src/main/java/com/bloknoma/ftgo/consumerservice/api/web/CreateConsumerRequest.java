package com.bloknoma.ftgo.consumerservice.api.web;

import com.bloknoma.ftgo.common.PersonName;

// 고객 생성 요청?
public class CreateConsumerRequest {
    private PersonName name;

    private CreateConsumerRequest() {
    }

    public CreateConsumerRequest(PersonName name) {
        this.name = name;
    }

    public PersonName getName() {
        return name;
    }

    public void setName(PersonName name) {
        this.name = name;
    }
}
