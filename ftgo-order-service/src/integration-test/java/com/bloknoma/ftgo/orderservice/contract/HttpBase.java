package com.bloknoma.ftgo.orderservice.contract;

import com.bloknoma.ftgo.common.CommonJsonMapperInitializer;
import com.bloknoma.ftgo.orderservice.OrderDetailsMother;
import com.bloknoma.ftgo.orderservice.domain.OrderService;
import com.bloknoma.ftgo.orderservice.domain.repository.OrderRepository;
import com.bloknoma.ftgo.orderservice.web.OrderController;
import io.eventuate.common.json.mapper.JSonMapper;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.util.Optional;

import static java.util.Optional.empty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// 목 컨트롤러 생성
public abstract class HttpBase {

    // 컨트롤러, 메시지 컨버터 셋업
    private StandaloneMockMvcBuilder controllers(Object... controllers) {
        CommonJsonMapperInitializer.registerMoneyModule();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(JSonMapper.objectMapper);
        return MockMvcBuilders.standaloneSetup(controllers).setMessageConverters(converter);
    }

    @Before
    public void setup() {
        // 목 설정
        OrderService orderService = mock(OrderService.class);
        OrderRepository orderRepository = mock(OrderRepository.class);

        OrderController orderController = new OrderController(orderService, orderRepository);

        // 설정
        when(orderRepository.findById(OrderDetailsMother.ORDER_ID)).thenReturn(Optional.of(OrderDetailsMother.CHICKEN_VINDALOO_ORDER));
        when(orderRepository.findById(555L)).thenReturn(empty());
        RestAssuredMockMvc.standaloneSetup(controllers(orderController));
    }
}
