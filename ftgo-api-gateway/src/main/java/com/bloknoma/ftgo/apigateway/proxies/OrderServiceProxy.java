package com.bloknoma.ftgo.apigateway.proxies;

import com.bloknoma.ftgo.apigateway.orders.OrderDestinations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OrderServiceProxy {

    private OrderDestinations orderDestinations;

    private WebClient client;

    public OrderServiceProxy(OrderDestinations orderDestinations, WebClient client) {
        this.orderDestinations = orderDestinations;
        this.client = client;
    }

    // 주문 조회
    public Mono<OrderInfo> findOrderByid(String orderId) {
        // 주문 서비스 호출
        Mono<ClientResponse> response = client
                .get()
                .uri(orderDestinations.getOrderServiceUrl() + "/orders/{orderId}", orderId)
                .exchange();
        return response.flatMap(resp -> {
            switch (resp.statusCode()) {
                case OK:
                    // 응답 본문을 OrderInfo로 변환
                    return resp.bodyToMono(OrderInfo.class);
                case NOT_FOUND:
                    return Mono.error(new OrderNotFoundException());
                default:
                    return Mono.error(new RuntimeException("Unknown" + resp.statusCode()));
            }
        });
    }
}
