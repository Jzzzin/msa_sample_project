package com.bloknoma.ftgo.kitchenservice.domain;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.kitchenservice.api.CreateTicket;
import com.bloknoma.ftgo.kitchenservice.api.TicketDetails;
import com.bloknoma.ftgo.kitchenservice.domain.repository.RestaurantRepository;
import com.bloknoma.ftgo.kitchenservice.messagehandlers.KitchenServiceMessageHandlersConfiguration;
import com.bloknoma.ftgo.kitchenservice.web.KitchenServiceWebConfiguration;
import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.sagas.common.SagaCommandHeaders;
import io.eventuate.tram.testutil.TestMessageConsumer;
import io.eventuate.tram.testutil.TestMessageConsumerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@SpringBootTest(classes = KitchenServiceMemoryIntegrationTest.TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KitchenServiceMemoryIntegrationTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${local.server.port}")
    private int port;

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Configuration
    @EnableAutoConfiguration
    @Import({KitchenServiceWebConfiguration.class, KitchenServiceMessageHandlersConfiguration.class,
            TramCommandProducerConfiguration.class,
            TramInMemoryConfiguration.class})
    public static class TestConfiguration {

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
                    .build();
        }
    }

    @Autowired
    private CommandProducer commandProducer;

    @Autowired
    private TestMessageConsumerFactory testMessageConsumerFactory;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void shouldCreateTicket() {

        long restaurantId = System.currentTimeMillis();
        Restaurant restaurant = new Restaurant(restaurantId, Collections.emptyList());

        restaurantRepository.save(restaurant);

        TestMessageConsumer testMessageConsumer = testMessageConsumerFactory.make();

        long orderId = 999;
        Money orderTotal = new Money(123);

        TicketDetails orderDetails = new TicketDetails();
        String messageId = commandProducer.send("kitchenService", null,
                new CreateTicket(restaurantId, orderId, orderDetails),
                testMessageConsumer.getReplyChannel(), withSagaCommandHeaders());

        testMessageConsumer.assertHasReplyTo(messageId);
    }

    private Map<String, String> withSagaCommandHeaders() {
        Map<String, String> result = new HashMap<>();
        result.put(SagaCommandHeaders.SAGA_TYPE, "MySagaType");
        result.put(SagaCommandHeaders.SAGA_ID, "MySagaId");
        return result;
    }

}
