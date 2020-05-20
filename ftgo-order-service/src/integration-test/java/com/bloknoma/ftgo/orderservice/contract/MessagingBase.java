package com.bloknoma.ftgo.orderservice.contract;

import com.bloknoma.ftgo.orderservice.api.events.OrderCreatedEvent;
import com.bloknoma.ftgo.orderservice.domain.OrderDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.springcloudcontractsupport.EventuateContractVerifierConfiguration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_ORDER;
import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_ORDER_DETAILS;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT_NAME;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessagingBase.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class MessagingBase {

    @Configuration
    @EnableAutoConfiguration
    @Import({EventuateContractVerifierConfiguration.class, TramEventsPublisherConfiguration.class, TramInMemoryConfiguration.class})
    public static class TestConfiguration {

        @Bean
        public OrderDomainEventPublisher orderAggregateEventPublisher(DomainEventPublisher eventPublisher) {
            return new OrderDomainEventPublisher(eventPublisher);
        }
    }

    @Autowired
    private OrderDomainEventPublisher orderAggregateEventPublisher;

    // contract 에 의해서 호출되는 메서드
    protected void orderCreated() {
        orderAggregateEventPublisher.publish(CHICKEN_VINDALOO_ORDER,
                Collections.singletonList(new OrderCreatedEvent(CHICKEN_VINDALOO_ORDER_DETAILS, AJANTA_RESTAURANT_NAME)));
    }
}
