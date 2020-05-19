package com.bloknoma.ftgo.cqrs.orderhistory.web;

import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistory;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryDao;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryFilter;
import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static java.util.stream.Collectors.toList;

// REST API
@RestController
@RequestMapping(path = "/orders")
public class OrderHistoryController {

    private OrderHistoryDao orderHistoryDao;

    public OrderHistoryController(OrderHistoryDao orderHistoryDao) {
        this.orderHistoryDao = orderHistoryDao;
    }

    // 주문 내역 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<GetOrdersResponse> getOrders(@RequestParam(name = "consumerId") String consumerId) {
        OrderHistory orderHistory = orderHistoryDao.findOrderHistory(consumerId, new OrderHistoryFilter());
        return new ResponseEntity<>(new GetOrdersResponse(orderHistory.getOrders()
                    .stream()
                    .map(this::makeGetOrderResponse).collect(toList()),
                    orderHistory.getStartKey().orElse(null)),
                HttpStatus.OK);
    }

    private GetOrderResponse makeGetOrderResponse(Order order) {
        return new GetOrderResponse(order.getOrderId(), order.getStatus(), order.getRestaurantId(), order.getRestaurantName());
    }

    // 주문 조회
    @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<GetOrderResponse> getOrder(@PathVariable String orderId) {
        return orderHistoryDao.findOrder(orderId)
                .map(order -> new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
