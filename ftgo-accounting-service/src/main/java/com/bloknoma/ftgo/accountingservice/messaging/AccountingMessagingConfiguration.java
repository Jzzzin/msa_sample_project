package com.bloknoma.ftgo.accountingservice.messaging;

import com.bloknoma.ftgo.accountingservice.domain.Account;
import com.bloknoma.ftgo.accountingservice.domain.AccountingServiceConfiguration;
import com.bloknoma.ftgo.common.CommonConfiguration;
import io.eventuate.javaclient.spring.EnableEventHandlers;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.commands.consumer.CommandDispatcherFactory;
import io.eventuate.tram.commands.consumer.TramCommandConsumerConfiguration;
import io.eventuate.tram.consumer.common.DuplicateMessageDetector;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory;
import io.eventuate.tram.events.subscriber.TramEventSubscriberConfiguration;
import io.eventuate.tram.sagas.eventsourcingsupport.SagaReplyRequestedEventSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Collections;

@Configuration
@EnableEventHandlers
@Import({AccountingServiceConfiguration.class, CommonConfiguration.class, TramEventSubscriberConfiguration.class, TramCommandConsumerConfiguration.class})
public class AccountingMessagingConfiguration {

    @Bean
    public AccountingEventConsumer accountingEventConsumer() {
        return new AccountingEventConsumer();
    }

    @Bean
    public DomainEventDispatcher domainEventDispatcher(AccountingEventConsumer accountingEventConsumer, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("accountingServiceDomainEventDispatcher", accountingEventConsumer.domainEventHandlers());
    }

    @Bean
    public AccountingServiceCommandHandler accountCommandHandler() {
        return new AccountingServiceCommandHandler();
    }

    @Bean
    public CommandDispatcher commandDispatcher(AccountingServiceCommandHandler target,
                                               AccountServiceChannelConfiguration data, CommandDispatcherFactory commandDispatcherFactory) {
        return commandDispatcherFactory.make(data.getCommandDispatcherId(), target.commandHandlers());
    }

    @Bean
    public DuplicateMessageDetector duplicateMessageDetector() {
        return new NoopDuplicateMessageDetector();
    }

    @Bean
    public AccountServiceChannelConfiguration accountServiceChannelConfiguration() {
        return new AccountServiceChannelConfiguration("accountCommandDispatcher", "accountCommandChannel");
    }

    @Bean
    public SagaReplyRequestedEventSubscriber sagaReplyRequestedEventSubscriber() {
        return new SagaReplyRequestedEventSubscriber("accountingServiceSagaReplyRequestedEventSubscriver", Collections.singleton(Account.class.getName()));
    }
}
