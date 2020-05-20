package com.bloknoma.ftgo.orderservice.web;

import com.bloknoma.ftgo.common.CommonJsonMapperInitializer;
import com.bloknoma.ftgo.orderservice.OrderDetailsMother;
import com.bloknoma.ftgo.orderservice.domain.OrderService;
import com.bloknoma.ftgo.orderservice.domain.repository.OrderRepository;
import io.eventuate.common.json.mapper.JSonMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.Optional;

import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_ORDER;
import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.CHICKEN_VINDALOO_ORDER_TOTAL;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// unit test
public class OrderControllerTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private OrderController orderController;

    // 목 설정 및 컨트롤러 구성
    @Before
    public void setUp() throws Exception {
        orderService = mock(OrderService.class);
        orderRepository = mock(OrderRepository.class);
        orderController = new OrderController(orderService, orderRepository);
    }

    // 주문 조회 테스트
    @Test
    public void shouldFindOrder() {
        // 주문 조회 목 설정
        when(orderRepository.findById(1L)).thenReturn(Optional.of(CHICKEN_VINDALOO_ORDER));

        given()
                .standaloneSetup(configureControllers(orderController))
        .when()
                .get("/orders/1")
        .then()
                .statusCode(200)
                .body("orderId", equalTo(new Long(OrderDetailsMother.ORDER_ID).intValue()))
                .body("state", equalTo(OrderDetailsMother.CHICKEN_VINDALOO_ORDER_STATE.name()))
                .body("orderTotal", equalTo(CHICKEN_VINDALOO_ORDER_TOTAL.asString()));

    }

    // 주문 조회 실패 테스트
    @Test
    public void shouldFindNotOrder() {
        // 주문 조회 실패 목 설정
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        given()
                .standaloneSetup(configureControllers(new OrderController(orderService, orderRepository)))
        .when()
                .get("/orders/1")
        .then()
                .statusCode(404);
    }

    // JSON 컨버터 설정
    private StandaloneMockMvcBuilder configureControllers(Object... controllers) {
        CommonJsonMapperInitializer.registerMoneyModule();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(JSonMapper.objectMapper);
        return MockMvcBuilders.standaloneSetup(controllers).setMessageConverters(converter);
    }
}
