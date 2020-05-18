package com.bloknoma.ftgo.consumerservice.web;

import com.bloknoma.ftgo.common.PersonName;
import com.bloknoma.ftgo.consumerservice.api.web.CreateConsumerResponse;

public class GetConsumerResponse extends CreateConsumerResponse{
    private PersonName name;

    public GetConsumerResponse(PersonName name) {
        this.name = name;
    }

    public PersonName getName() {
        return name;
    }
}
