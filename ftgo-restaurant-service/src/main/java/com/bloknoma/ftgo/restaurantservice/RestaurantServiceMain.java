package com.bloknoma.ftgo.restaurantservice;

import com.bloknoma.eventstore.examples.customersandorders.commonswagger.CommonSwaggerConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.tram.jdbckafka.TramJdbcKafkaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
@EnableAutoConfiguration
@Import({TramJdbcKafkaConfiguration.class, CommonSwaggerConfiguration.class})
@ComponentScan
public class RestaurantServiceMain {

    @Bean
    @Primary // conflicts with _halObjectMapper
    public ObjectMapper objectMapper() {
        return JSonMapper.objectMapper;
    }

    public static void main(String[] args) {
        SpringApplication.run(RestaurantServiceMain.class, args);
    }
}
