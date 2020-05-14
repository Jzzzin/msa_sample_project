package com.bloknoma.ftgo.orderservice.main;

import com.bloknoma.eventstore.examples.customersandorders.commonswagger.CommonSwaggerConfiguration;
import com.bloknoma.ftgo.orderservice.grpc.GrpcConfiguration;
import com.bloknoma.ftgo.orderservice.messaging.OrderServiceMessagingConfiguration;
import com.bloknoma.ftgo.orderservice.service.OrderCommandHandlersConfiguration;
import com.bloknoma.ftgo.orderservice.web.OrderWebConfiguration;
import io.eventuate.tram.jdbckafka.TramJdbcKafkaConfiguration;
import io.microservices.canvas.extractor.spring.annotations.ServiceDescription;
import io.microservices.canvas.springmvc.MicroserviceCanvasWebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({OrderWebConfiguration.class, OrderCommandHandlersConfiguration.class, OrderServiceMessagingConfiguration.class,
        TramJdbcKafkaConfiguration.class, CommonSwaggerConfiguration.class, GrpcConfiguration.class,
        MicroserviceCanvasWebConfiguration.class})
@ServiceDescription(description = "Manages Orders", capabilities = "Order Management")
public class OrderServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceMain.class, args);
    }
}
