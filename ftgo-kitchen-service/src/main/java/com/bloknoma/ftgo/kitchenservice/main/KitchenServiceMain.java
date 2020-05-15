package com.bloknoma.ftgo.kitchenservice.main;

import com.bloknoma.eventstore.examples.customersandorders.commonswagger.CommonSwaggerConfiguration;
import com.bloknoma.ftgo.kitchenservice.messagehandlers.KitchenServiceMessageHandlersConfiguration;
import com.bloknoma.ftgo.kitchenservice.web.KitchenServiceWebConfiguration;
import io.eventuate.tram.jdbckafka.TramJdbcKafkaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({KitchenServiceWebConfiguration.class, KitchenServiceMessageHandlersConfiguration.class,
        TramJdbcKafkaConfiguration.class, CommonSwaggerConfiguration.class})
public class KitchenServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(KitchenServiceMain.class, args);
    }
}
