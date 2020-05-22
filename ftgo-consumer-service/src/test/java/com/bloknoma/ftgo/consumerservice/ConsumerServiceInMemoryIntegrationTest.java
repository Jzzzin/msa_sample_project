package com.bloknoma.ftgo.consumerservice;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.common.PersonName;
import com.bloknoma.ftgo.consumerservice.api.ConsumerServiceChannels;
import com.bloknoma.ftgo.consumerservice.api.ValidateOrderByConsumer;
import com.bloknoma.ftgo.consumerservice.api.web.CreateConsumerRequest;
import com.bloknoma.ftgo.consumerservice.web.ConsumerWebConfiguration;
import io.eventuate.tram.commands.producer.CommandProducer;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.testutil.TestMessageConsumer;
import io.eventuate.tram.testutil.TestMessageConsumerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

// unit test
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConsumerServiceInMemoryIntegrationTest.TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConsumerServiceInMemoryIntegrationTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${local.server.port}")
    private int port;

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Configuration
    @Import({ConsumerWebConfiguration.class,
            TramCommandProducerConfiguration.class,
            TramInMemoryConfiguration.class})
    public static class TestConfiguration {

        @Bean
        public TestMessageConsumerFactory testMessageConsumerFactory() {
            return new TestMessageConsumerFactory();
        }
    }

    @Autowired
    private CommandProducer commandProducer;

    @Autowired
    private TestMessageConsumerFactory testMessageConsumerFactory;

    // 고객 추가
    @Test
    public void shouldCreateConsumer() {

        String postUrl = baseUrl("/consumers");

        Integer consumerId =
                given()
                    .body(new CreateConsumerRequest(new PersonName("John", "Doe")))
                    .contentType("application/json")
                .when()
                    .post(postUrl)
                .then()
                    .statusCode(200)
                .extract()
                    .path("consumerId");

        assertNotNull(consumerId);

        TestMessageConsumer testMessageConsumer = testMessageConsumerFactory.make();

        long orderId = 999;
        Money orderTotal = new Money(123);

        String messageId = commandProducer.send(ConsumerServiceChannels.consumerServiceChannel, null,
                new ValidateOrderByConsumer(consumerId, orderId, orderTotal),
                testMessageConsumer.getReplyChannel(),
                Collections.emptyMap());

        testMessageConsumer.assertHasReplyTo(messageId);

    }
}
