package com.bloknoma.ftgo.cqrs.orderhistory.main;

import com.bloknoma.eventstore.examples.customersandorders.commonswagger.CommonSwaggerConfiguration;
import com.bloknoma.ftgo.cqrs.orderhistory.messaging.OrderHistoryServiceMessagingConfiguration;
import com.bloknoma.ftgo.cqrs.orderhistory.web.OrderHistoryWebConfiguration;
import io.eventuate.tram.consumer.common.TramConsumerCommonConfiguration;
import io.eventuate.tram.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({OrderHistoryWebConfiguration.class,
        OrderHistoryServiceMessagingConfiguration.class,
        CommonSwaggerConfiguration.class,
        TramConsumerCommonConfiguration.class,
        EventuateTramKafkaMessageConsumerConfiguration.class})
public class OrderHistoryServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(OrderHistoryServiceMain.class, args);
    }
}
