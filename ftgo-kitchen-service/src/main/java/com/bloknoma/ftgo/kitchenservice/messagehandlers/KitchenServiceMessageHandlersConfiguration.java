package com.bloknoma.ftgo.kitchenservice.messagehandlers;

import com.bloknoma.ftgo.common.CommonConfiguration;
import com.bloknoma.ftgo.kitchenservice.domain.KitchenDomainConfiguration;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import io.eventuate.tram.sagas.participant.SagaParticipantConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({KitchenDomainConfiguration.class, SagaParticipantConfiguration.class, CommonConfiguration.class, TramEventSubscriberConfiguration.class})
public class KitchenServiceMessageHandlersConfiguration {

    @Bean
    public KitchenServiceEventConsumer ticketEventConsumer() {
        return new KitchenServiceEventConsumer();
    }

    @Bean
    public KitchenServiceCommandHandler kitchenServiceCommandHandler() {
        return new KitchenServiceCommandHandler();
    }

    @Bean
    public SagaCommandDispatcher kitchenServiceSagaCommandDispatcher(KitchenServiceCommandHandler kitchenServiceCommandHandler, SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {
        return sagaCommandDispatcherFactory.make("kitchenServiceCommands", kitchenServiceCommandHandler.commandHandlers());
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcher(KitchenServiceEventConsumer kitchenServiceEventConsumer, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("kitchenServiceEvents", kitchenServiceEventConsumer.domainEventHandlers());
    }
}
