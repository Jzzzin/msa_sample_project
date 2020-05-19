package com.bloknoma.ftgo.apigateway.orders;

import com.bloknoma.ftgo.apigateway.proxies.*;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.util.Optional;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

// 주문 조회 핸들러
public class OrderHandlers {

    private OrderServiceProxy orderService;
    private KitchenService kitchenService;
    private DeliveryService deliveryService;
    private AccountingService accountingService;

    public OrderHandlers(OrderServiceProxy orderService, KitchenService kitchenService, DeliveryService deliveryService, AccountingService accountingService) {
        this.orderService = orderService;
        this.kitchenService = kitchenService;
        this.deliveryService = deliveryService;
        this.accountingService = accountingService;
    }

    // 주문 조회
    public Mono<ServerResponse> getOrderDetails(ServerRequest serverRequest) {
        String orderId = serverRequest.pathVariable("orderId");

        // 주문 조회 처리
        Mono<OrderInfo> orderInfo = orderService.findOrderByid(orderId);

        // 주문에 대한 티켓 정보 조회
        Mono<Optional<TicketInfo>> ticketInfo = kitchenService
                .findTicketById(orderId)
                .map(Optional::of) // TicketInfo 를 Optional<TicketInfo> 로 변환
                .onErrorReturn(Optional.empty()); // 서비스 호출 실패 시 Optional.empty() 반환

        // 주문에 대한 배달 정보 조회
        Mono<Optional<DeliveryInfo>> deliveryInfo = deliveryService
                .findDeliveryByOrderId(orderId)
                .map(Optional::of)
                .onErrorReturn(Optional.empty());

        // 주문에 대한 청구 정보 조회
        Mono<Optional<BillInfo>> billInfo = accountingService
                .findBillByOrderId(orderId)
                .map(Optional::of)
                .onErrorReturn(Optional.empty());

        // 하나의 튜플로 조합
        Mono<Tuple4<OrderInfo, Optional<TicketInfo>, Optional<DeliveryInfo>, Optional<BillInfo>>> combined =
                Mono.zip(orderInfo, ticketInfo, deliveryInfo, billInfo);

        // 튜플4 를 OrderDetails 로 변환
        Mono<OrderDetails> orderDetails = combined.map(OrderDetails::makeOrderDetails);

        // OrderDetails 를 ServerResponse 로 변환
        return orderDetails.flatMap(od -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromObject(od))
        ).onErrorResume(OrderNotFoundException.class, e -> ServerResponse.notFound().build());
    }
}
