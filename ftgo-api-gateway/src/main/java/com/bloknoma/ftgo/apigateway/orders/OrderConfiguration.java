package com.bloknoma.ftgo.apigateway.orders;

import com.bloknoma.ftgo.apigateway.proxies.AccountingService;
import com.bloknoma.ftgo.apigateway.proxies.DeliveryService;
import com.bloknoma.ftgo.apigateway.proxies.KitchenService;
import com.bloknoma.ftgo.apigateway.proxies.OrderServiceProxy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@EnableConfigurationProperties(OrderDestinations.class)
public class OrderConfiguration {

    // orders 요청 라우팅
    @Bean
    public RouteLocator orderProxyRouting(RouteLocatorBuilder builder, OrderDestinations orderDestinations) {
        return builder.routes()
                .route(r -> r.path("/orders").and().method("POST").uri(orderDestinations.getOrderServiceUrl()))
                .route(r -> r.path("/orders").and().method("PUT").uri(orderDestinations.getOrderServiceUrl()))
                .route(r -> r.path("/orders/**").and().method("POST").uri(orderDestinations.getOrderServiceUrl()))
                .route(r -> r.path("/orders/**").and().method("PUT").uri(orderDestinations.getOrderServiceUrl()))
                .route(r -> r.path("/orders").and().method("GET").uri(orderDestinations.getOrderHistoryServiceUrl()))
                .build();
    }

    // orders 조회 핸들러로 라우팅
    @Bean
    public RouterFunction<ServerResponse> orderHandlerRouting(OrderHandlers orderHandlers) {
        return RouterFunctions.route(GET("/orders/{orderId}"), orderHandlers::getOrderDetails);
    }

    // 사용자 정의 요청 처리 로직이 구현된 핸들러
    @Bean
    public OrderHandlers orderHandlers(OrderServiceProxy orderService, KitchenService kitchenService, DeliveryService deliveryService, AccountingService accountingService) {
        return new OrderHandlers(orderService, kitchenService, deliveryService, accountingService);
    }

    // 프록시 서비스 에서 사용하는 webClient
    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}
