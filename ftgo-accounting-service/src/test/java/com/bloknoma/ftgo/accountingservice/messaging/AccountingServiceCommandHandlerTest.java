package com.bloknoma.ftgo.accountingservice.messaging;

import com.bloknoma.ftgo.accountingservice.domain.Account;
import com.bloknoma.ftgo.accountingservice.domain.AccountCommand;
import com.bloknoma.ftgo.accountservice.api.AccountingServiceChannels;
import com.bloknoma.ftgo.accountservice.api.AuthorizeCommand;
import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.consumerservice.api.ConsumerCreated;
import io.eventuate.sync.AggregateRepository;
import io.eventuate.javaclient.spring.jdbc.EmbeddedTestAggregateStoreConfiguration;
import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.sagas.common.SagaCommandHeaders;
import io.eventuate.tram.testutil.TestMessageConsumer;
import io.eventuate.tram.testutil.TestMessageConsumerFactory;
import io.eventuate.util.test.async.Eventually;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// unit test
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountingServiceCommandHandlerTest.AccountingServiceCommandHandlerTestConfiguration.class)
public class AccountingServiceCommandHandlerTest {

    @Configuration
    @EnableAutoConfiguration
    @Import({AccountingMessagingConfiguration.class,
            TramCommandProducerConfiguration.class,
            EmbeddedTestAggregateStoreConfiguration.class,
            TramEventsPublisherConfiguration.class, // TODO
            TramInMemoryConfiguration.class})
    static public class AccountingServiceCommandHandlerTestConfiguration {

        @Bean
        public TestMessageConsumerFactory testMessageConsumerFactory() {
            return new TestMessageConsumerFactory();
        }

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(EmbeddedDatabaseType.H2)
                    .addScript("eventuate-tram-embedded-schema.sql")
                    .addScript("eventuate-tram-sagas-embedded.sql")
                    .addScript("eventuate-embedded-schema.sql")
                    .build();
        }
    }

    @Autowired
    private CommandProducer commandProducer;

    @Autowired
    private TestMessageConsumerFactory testMessageConsumerFactory;

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    @Autowired
    private AggregateRepository<Account, AccountCommand> accountRepository;

    @Test
    public void shouldReply() {

        TestMessageConsumer testMessageConsumer = testMessageConsumerFactory.make();

        long consumerId = System.currentTimeMillis();
        long orderId = 102L;

        domainEventPublisher.publish("com.bloknoma.ftgo.consumerservice.domain.Consumer", consumerId, Collections.singletonList(new ConsumerCreated()));

        Eventually.eventually(() -> {
            accountRepository.find(Long.toString(consumerId));
        });

        Money orderTotal = new Money(123);

        String messageId = commandProducer.send(AccountingServiceChannels.accountingServiceChannel, null,
                new AuthorizeCommand(consumerId, orderId, orderTotal),
                testMessageConsumer.getReplyChannel(), withSagaCommandHeaders());

        testMessageConsumer.assertHasReplyTo(messageId);
    }

    // TODO duplicate

    private Map<String, String> withSagaCommandHeaders() {
        Map<String, String> result = new HashMap<>();
        result.put(SagaCommandHeaders.SAGA_TYPE, "MySagaType");
        result.put(SagaCommandHeaders.SAGA_ID, "MySagaId");
        return result;
    }
}
