package com.bloknoma.ftgo.consumerservice.main;

import com.bloknoma.eventstore.examples.customersandorders.commonswagger.CommonSwaggerConfiguration;
import com.bloknoma.ftgo.consumerservice.web.ConsumerWebConfiguration;
import io.eventuate.tram.jdbckafka.TramJdbcKafkaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ConsumerWebConfiguration.class, TramJdbcKafkaConfiguration.class, CommonSwaggerConfiguration.class})
public class ConsumerServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerServiceMain.class, args);
    }
}
