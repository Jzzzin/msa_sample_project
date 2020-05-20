package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.consumerservice.api.ConsumerServiceChannels;
import com.bloknoma.ftgo.consumerservice.api.ValidateOrderByConsumer;
import com.bloknoma.ftgo.orderservice.domain.repository.OrderRepository;
import com.bloknoma.ftgo.orderservice.domain.repository.RestaurantRepository;
import com.bloknoma.ftgo.orderservice.messaging.OrderServiceMessagingConfiguration;
import com.bloknoma.ftgo.orderservice.service.OrderCommandHandlersConfiguration;
import com.bloknoma.ftgo.orderservice.web.MenuItemIdAndQuantity;
import com.bloknoma.ftgo.orderservice.web.OrderWebConfiguration;
import com.bloknoma.ftgo.restaurantservice.events.MenuItem;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantCreated;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;
import com.bloknoma.ftgo.testutil.FtgoTestUtil;
import io.eventuate.tram.commands.common.CommandMessageHeaders;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.testutil.TestMessageConsumerFactory;
import io.eventuate.util.test.async.Eventually;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.function.Predicate;

// unit test
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceIntegrationTest.TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "eventuate.database.schema=eventuate")
public class OrderServiceIntegrationTest {

    public static final String RESTAURANT_ID = "1";
    private static final String CHICKEN_VINDALOO_MENU_ITEM_ID = "1";

    @Value("${local.server.port}")
    private int port;

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @Configuration
    @EnableAutoConfiguration
    @Import({OrderWebConfiguration.class, OrderServiceMessagingConfiguration.class, OrderCommandHandlersConfiguration.class,
            TramCommandProducerConfiguration.class, TramInMemoryConfiguration.class})
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

        @Bean
        public TestMessageConsumer2 mockConsumerService() {
            return new TestMessageConsumer2("mockConsumerService", ConsumerServiceChannels.consumerServiceChannel);
        }
    }

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    @Qualifier("mockConsumerService")
    private TestMessageConsumer2 mockConsumerService;

    // 주문 생성
    @Test
    public void shouldCreateOrder() {
        // 레스토랑 생성 이벤트
        domainEventPublisher.publish("com.bloknoma.ftgo.restaurantservice.domain.Restaurant", RESTAURANT_ID,
                Collections.singletonList(new RestaurantCreated("Ajanta",
                        new RestaurantMenu(Collections.singletonList(new MenuItem(CHICKEN_VINDALOO_MENU_ITEM_ID, "Chicken Vindaloo", new Money("12.34")))))));

        // 레스토랑 추가 확인
        Eventually.eventually(() -> {
            FtgoTestUtil.assertPresent(restaurantRepository.findById(Long.parseLong(RESTAURANT_ID)));
        });

        long consumerId = 1511300065921L;

        // 주문 생성
        Order order = orderService.createOrder(consumerId, Long.parseLong(RESTAURANT_ID), Collections.singletonList(new MenuItemIdAndQuantity(CHICKEN_VINDALOO_MENU_ITEM_ID, 5)));

        // 주문 생성 확인
        FtgoTestUtil.assertPresent(orderRepository.findById(order.getId()));

        String expectedPayload = "{\"consumerId\":1511300065921,\"orderId\":1,\"orderTotal\":\"61.70\"}";

        Message message = mockConsumerService.assertMessageReceived(
                commandMessageOfType(ValidateOrderByConsumer.class.getName()).and(withPayload(expectedPayload)));

        System.out.println("message=" + message);
    }

    private Predicate<? super Message> withPayload(String expectedPayload) {
        return (m) -> expectedPayload.equals(m.getPayload());
    }

    private Predicate<Message> commandMessageOfType(String commandType) {
        return (m) -> m.getRequiredHeader(CommandMessageHeaders.COMMAND_TYPE).equals(commandType);
    }
}
