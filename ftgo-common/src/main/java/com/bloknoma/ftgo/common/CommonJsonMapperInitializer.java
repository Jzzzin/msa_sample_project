package com.bloknoma.ftgo.common;

import io.eventuate.common.json.mapper.JSonMapper;

import javax.annotation.PostConstruct;

// 통화 모듈 설정
public class CommonJsonMapperInitializer {

    @PostConstruct
    public void initialize() {
        registerMoneyModule();
    }

    public static void registerMoneyModule() {
        JSonMapper.objectMapper.registerModule(new MoneyModule());
    }
}
