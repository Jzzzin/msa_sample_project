package com.bloknoma.ftgo.apigateway.contract;

import com.bloknoma.ftgo.apigateway.orders.OrderDestinations;
import com.bloknoma.ftgo.apigateway.proxies.OrderInfo;
import com.bloknoma.ftgo.apigateway.proxies.OrderNotFoundException;
import com.bloknoma.ftgo.apigateway.proxies.OrderServiceProxy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.Assert.assertEquals;

// unit test - contracts
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(ids = {"com.bloknoma.ftgo:ftgo-order-service-contracts"})
@DirtiesContext
public class OrderServiceProxyIntegrationTest {

    @Value("${stubrunner.runningstubs.ftgo-order-service-contracts.port}")
    private int port;

    private OrderDestinations orderDestinations;
    private OrderServiceProxy orderService;

    // 서비스 URL 설정
    @Before
    public void setUp() throws Exception {
        orderDestinations = new OrderDestinations();
        String orderServiceUrl = "http://localhost:" + port;
        System.out.println("orderServiceUrl=" + orderServiceUrl);
        orderDestinations.setOrderServiceUrl(orderServiceUrl);
        orderService = new OrderServiceProxy(orderDestinations, WebClient.create());
    }

    // 주문 조회
    @Test
    public void shouldVerifyExistingOrder() {
        OrderInfo result = orderService.findOrderByid("99").block();
        assertEquals("99", result.getOrderId());
        assertEquals("APPROVAL_PENDING", result.getState());
    }

    @Test(expected = OrderNotFoundException.class)
    public void shouldFailToFindMissingOrder() {
        orderService.findOrderByid("555").block();
    }
}
