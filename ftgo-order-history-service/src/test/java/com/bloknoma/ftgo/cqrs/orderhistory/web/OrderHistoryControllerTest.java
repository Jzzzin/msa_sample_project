package com.bloknoma.ftgo.cqrs.orderhistory.web;

import com.bloknoma.ftgo.common.CommonJsonMapperInitializer;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryDao;
import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.Order;
import io.eventuate.common.json.mapper.JSonMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderHistoryControllerTest {

    private OrderHistoryDao orderHistoryDao;
    private OrderHistoryController orderHistoryController;

    @Before
    public void setUp() {
        orderHistoryDao = mock(OrderHistoryDao.class);
        orderHistoryController = new OrderHistoryController(orderHistoryDao);
    }

    // 주문 조회
    @Test
    public void testGetOrder() {
        // 목 설정
        when(orderHistoryDao.findOrder("1")).thenReturn(Optional.of(new Order("1", null, null, null, null, 101L, "Ajanta")));

        given()
                .standaloneSetup(configureControllers(orderHistoryController))
                .when()
                    .get("/orders/1")
                .then()
                    .statusCode(200)
                    .body("restaurantName", equalTo("Ajanta"));
    }

    // TODO move to test library

    private StandaloneMockMvcBuilder configureControllers(Object... controllers) {
        CommonJsonMapperInitializer.registerMoneyModule();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(JSonMapper.objectMapper);
        return MockMvcBuilders.standaloneSetup(controllers).setMessageConverters(converter);
    }
}
