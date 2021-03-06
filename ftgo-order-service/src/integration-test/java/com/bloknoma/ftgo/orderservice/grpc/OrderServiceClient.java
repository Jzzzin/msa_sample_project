package com.bloknoma.ftgo.orderservice.grpc;

import com.bloknoma.ftgo.orderservice.web.MenuItemIdAndQuantity;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class OrderServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceClient.class.getName());

    private final ManagedChannel channel;
    // stub 구성
    private final OrderServiceGrpc.OrderServiceBlockingStub clientStub;

    public OrderServiceClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        clientStub = OrderServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public long createOrder(long consumerId, long restaurantId, List<MenuItemIdAndQuantity> lineItems) {
        CreateOrderRequest.Builder builder = CreateOrderRequest.newBuilder()
                .setConsumerId(consumerId)
                .setRestaurantId(restaurantId);
        CreateOrderRequest request = builder
                .build();
        IntStream.range(0, lineItems.size()).forEach(idx -> {
            MenuItemIdAndQuantity li = lineItems.get(idx);
            builder.setLineItems(idx, LineItem.newBuilder().setQuantity(li.getQuantity()).setMenuItemId(li.getMenuItemId()).build());
        });
        CreateOrderReply response;
        response = clientStub.createOrder(request);
        return response.getOrderId();
    }
}
