package com.bloknoma.ftgo.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// json mapper 빈 설정
@Configuration
public class CommonConfiguration {

    @Bean
    public CommonJsonMapperInitializer commonJsonMapperInitializer() {
        return new CommonJsonMapperInitializer();

    }
}
