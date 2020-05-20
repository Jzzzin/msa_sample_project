package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.kitchenservice.api.CreateTicket;
import com.bloknoma.ftgo.kitchenservice.api.CreateTicketReply;
import com.bloknoma.ftgo.kitchenservice.api.TicketDetails;
import com.bloknoma.ftgo.kitchenservice.api.TicketLineItem;
import com.bloknoma.ftgo.orderservice.OrderDetailsMother;
import com.bloknoma.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import io.eventuate.tram.springcloudcontractsupport.EventuateContractVerifierConfiguration;
import io.eventuate.tram.springcloudcontractsupport.EventuateTramRoutesConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.BatchStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import java.util.Collections;

import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_QUANTITY;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.CHICKEN_VINDALOO;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.CHICKEN_VINDALOO_MENU_ITEM_ID;
import static org.junit.Assert.assertEquals;

// unit test - contracts
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KitchenServiceProxyIntegrationTest.TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = {"com.bloknoma.ftgo:ftgo-kitchen-service-contracts"})
@DirtiesContext
public class KitchenServiceProxyIntegrationTest {

    @Configuration
    @EnableAutoConfiguration
    @Import({TramCommandProducerConfiguration.class,
            TramInMemoryConfiguration.class, EventuateContractVerifierConfiguration.class})
    public static class TestConfiguration {

        // TramSagaInMemoryConfiguration
        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
            return builder.setType(EmbeddedDatabaseType.H2)
                    .addScript("eventuate-tram-embedded-schema.sql")
                    .addScript("eventuate-tram-sagas-embedded.sql")
                    .build();
        }

        @Bean
        public EventuateTramRoutesConfigurer eventuateTramRoutesConfigurer(BatchStubRunner batchStubRunner) {
            return new EventuateTramRoutesConfigurer(batchStubRunner);
        }

        @Bean
        public SagaMessagingTestHelper sagaMessagingTestHelper() {
            return new SagaMessagingTestHelper();
        }

        @Bean
        public SagaCommandProducer sagaCommandProducer() {
            return new SagaCommandProducer();
        }

        @Bean
        public KitchenServiceProxy kitchenServiceProxy() {
            return new KitchenServiceProxy();
        }
    }

    @Autowired
    private SagaMessagingTestHelper sagaMessagingTestHelper;

    @Autowired
    private KitchenServiceProxy kitchenServiceProxy;

    // 티켓 생성
    @Test
    public void shouldSuccessfullyCreateTicket() {
        // 커맨드 생성
        CreateTicket command = new CreateTicket(AJANTA_ID, OrderDetailsMother.ORDER_ID,
                new TicketDetails(Collections.singletonList(new TicketLineItem(CHICKEN_VINDALOO_MENU_ITEM_ID, CHICKEN_VINDALOO, CHICKEN_VINDALOO_QUANTITY))));
        CreateTicketReply expectedReply = new CreateTicketReply(OrderDetailsMother.ORDER_ID);
        String sagaType = CreateOrderSaga.class.getName();

        // 커맨드 전송
        CreateTicketReply reply = sagaMessagingTestHelper.sendAndReceiveCommand(kitchenServiceProxy.create, command, CreateTicketReply.class, sagaType);

        assertEquals(expectedReply, reply);

    }
}
