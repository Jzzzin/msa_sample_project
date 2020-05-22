package com.bloknoma.ftgo.orderservice.grpc;

import com.bloknoma.ftgo.orderservice.domain.Order;
import com.bloknoma.ftgo.orderservice.domain.OrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

// unit test
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceGrpIntegrationTestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class OrderServiceGrpIntegrationTest {

    @Autowired
    private OrderService orderService;

    // 주문 생성
    @Test
    public void shouldCreateOrder() {

        Order order = new Order(1L, 2L, Collections.emptyList());
        order.setId(101L);

        // 목 설정
        when(orderService.createOrder(1L, 2L, Collections.emptyList())).thenReturn(order);
        OrderServiceClient client = new OrderServiceClient("localhost", 50051);

        long orderId = client.createOrder(1, 2, Collections.emptyList());

        assertEquals(101L, orderId);
    }
}
