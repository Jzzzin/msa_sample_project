package com.bloknoma.ftgo.cqrs.orderhistory.messaging;

import com.bloknoma.ftgo.common.CommonConfiguration;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryDao;
import io.eventuate.tram.consumer.common.TramNoopDuplicateMessageDetectorConfiguration;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.events.subscriber.TramEventSubscriberConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CommonConfiguration.class, TramNoopDuplicateMessageDetectorConfiguration.class, TramEventSubscriberConfiguration.class})
public class OrderHistoryServiceMessagingConfiguration {

    @Bean
    public OrderHistoryEventHandlers orderHistoryEventHandlers(OrderHistoryDao orderHistoryDao) {
        return new OrderHistoryEventHandlers(orderHistoryDao);
    }

    @Bean
    public DomainEventDispatcher orderHistoryDomainEventDispatcher(OrderHistoryEventHandlers orderHistoryEventHandlers, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("orderHistoryDomainEventDispatcher", orderHistoryEventHandlers.domainEventHandlers());
    }
}
