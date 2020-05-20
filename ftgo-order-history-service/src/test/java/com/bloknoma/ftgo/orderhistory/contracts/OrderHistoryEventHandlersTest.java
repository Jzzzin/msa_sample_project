package com.bloknoma.ftgo.orderhistory.contracts;

import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryDao;
import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.Order;
import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.SourceEvent;
import com.bloknoma.ftgo.cqrs.orderhistory.messaging.OrderHistoryServiceMessagingConfiguration;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.messaging.common.ChannelMapping;
import io.eventuate.tram.messaging.common.DefaultChannelMapping;
import io.eventuate.tram.springcloudcontractsupport.EventuateContractVerifierConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.StubFinder;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static io.eventuate.util.test.async.Eventually.eventually;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// unit test - contracts
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderHistoryEventHandlersTest.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = {"com.bloknoma.ftgo:ftgo-order-service-contracts"})
@DirtiesContext
public class OrderHistoryEventHandlersTest {

    @Configuration
    @EnableAutoConfiguration
    @Import({OrderHistoryServiceMessagingConfiguration.class,
            TramCommandProducerConfiguration.class,
            TramInMemoryConfiguration.class,
            EventuateContractVerifierConfiguration.class})
    public static class TestConfiguration {

        @Bean
        public ChannelMapping channelMapping() {
            return new DefaultChannelMapping.DefaultChannelMappingBuilder().build();
        }

        @Bean
        public OrderHistoryDao orderHistoryDao() {
            // 목 DAO 생성
            return mock(OrderHistoryDao.class);
        }
    }

    @Autowired
    private StubFinder stubFinder;

    @Autowired
    private OrderHistoryDao orderHistoryDao;

    @Test
    public void shouldHandleOrderCreatedEvent() throws InterruptedException {

        when(orderHistoryDao.addOrder(any(Order.class), any(Optional.class))).thenReturn(false);

        // contract 에 정의된 label 로 이벤트 발생
        stubFinder.trigger("orderCreatedEvent");

        eventually(() -> {
            ArgumentCaptor<Order> orderArg = ArgumentCaptor.forClass(Order.class);
            ArgumentCaptor<Optional<SourceEvent>> sourceEventArg = ArgumentCaptor.forClass(Optional.class);
            // 이벤트 핸들러 작동 확인
            verify(orderHistoryDao).addOrder(orderArg.capture(), sourceEventArg.capture());

            Order order = orderArg.getValue();
            Optional<SourceEvent> sourceEvent = sourceEventArg.getValue();

            assertEquals("Ajanta", order.getRestaurantName());
        });
    }
}
