package com.bloknoma.ftgo.orderservice.grpc;

import com.bloknoma.ftgo.orderservice.domain.Order;
import com.bloknoma.ftgo.orderservice.domain.OrderService;
import com.bloknoma.ftgo.orderservice.web.MenuItemIdAndQuantity;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

import static java.util.stream.Collectors.toList;

// grpc 서버
public class OrderServiceServer {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceServer.class);

    private int port = 50051;
    private Server server;
    private OrderService orderService;

    public OrderServiceServer(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new OrderServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            logger.info("*** shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("*** server shut down");
        }
    }

    private class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

        // 주문 생성
        @Override
        public void createOrder(CreateOrderRequest req, StreamObserver<CreateOrderReply> responseObserver) {
            Order order = orderService.createOrder(req.getConsumerId(),
                    req.getRestaurantId(),
                    req.getLineItemsList().stream().map(x -> new MenuItemIdAndQuantity(x.getMenuItemId(), x.getQuantity())).collect(toList())
            );
            CreateOrderReply reply = CreateOrderReply.newBuilder().setOrderId(order.getId()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        // 주문 취소
        @Override
        public void cancelOrder(CancelOrderRequest req, StreamObserver<CancelOrderReply> responseObserver) {
            CancelOrderReply reply = CancelOrderReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        // 주문 수정
        @Override
        public void reviseOrder(ReviseOrderRequest req, StreamObserver<ReviseOrderReply> responseObserver) {
            ReviseOrderReply reply = ReviseOrderReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
