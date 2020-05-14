package com.bloknoma.ftgo.orderservice.messaging;

import com.bloknoma.ftgo.common.CommonJsonMapperInitializer;
import com.bloknoma.ftgo.orderservice.RestaurantMother;
import com.bloknoma.ftgo.orderservice.domain.OrderService;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantCreated;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;
import org.junit.Before;
import org.junit.Test;

import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT_NAME;
import static io.eventuate.tram.testing.DomainEventHandlerUnitTestSupport.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OrderEventConsumerTest {

    private OrderService orderService;
    private OrderEventConsumer orderEventConsumer;

    // 목설정 및 이벤트 컨슈머 설정
    @Before
    public void setUp() throws Exception {
        orderService = mock(OrderService.class);
        orderEventConsumer = new OrderEventConsumer(orderService);
    }

    // 레스토랑 추가 테스트
    @Test
    public void shouldCreateMenu() {

        CommonJsonMapperInitializer.registerMoneyModule();

        given()
                .eventHandlers(orderEventConsumer.domainEventHandlers())
        .when()
                .aggregate("com.bloknoma.ftgo.restaurantservice.domain.Restaurant", AJANTA_ID)
                .publishes(new RestaurantCreated(AJANTA_RESTAURANT_NAME, RestaurantMother.AJANTA_RESTAURANT_MENU))
        .then()
                .verify(() -> {
                    verify(orderService).createMenu(AJANTA_ID, AJANTA_RESTAURANT_NAME, new RestaurantMenu(RestaurantMother.AJANTA_RESTAURANT_MENU_ITEMS));
                });
    }
}
