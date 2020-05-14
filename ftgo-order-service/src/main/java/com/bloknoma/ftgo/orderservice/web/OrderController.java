package com.bloknoma.ftgo.orderservice.web;

import com.bloknoma.ftgo.orderservice.api.web.CreateOrderRequest;
import com.bloknoma.ftgo.orderservice.api.web.CreateOrderResponse;
import com.bloknoma.ftgo.orderservice.api.web.ReviseOrderRequest;
import com.bloknoma.ftgo.orderservice.domain.Order;
import com.bloknoma.ftgo.orderservice.domain.OrderService;
import com.bloknoma.ftgo.orderservice.domain.exception.OrderNotFoundException;
import com.bloknoma.ftgo.orderservice.domain.repository.OrderRepository;
import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static java.util.stream.Collectors.toList;

// 주문 rest 컨트롤러
@RestController
@RequestMapping(path = "/orders")
public class OrderController {

    private OrderService orderService;

    private OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    // 주문 생성
    @RequestMapping(method = RequestMethod.POST)
    public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.getConsumerId(),
                request.getRestaurantId(),
                request.getLineItems().stream().map(x -> new MenuItemIdAndQuantity(x.getMenuItemId(), x.getQuantity())).collect(toList())
        );
        return new CreateOrderResponse(order.getId());
    }

    // 주문 조회
    @RequestMapping(path = "/{orderId", method = RequestMethod.GET)
    public ResponseEntity<GetOrderResponse> getOrder(@PathVariable long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.map(o -> new ResponseEntity<>(makeGetOrderResponse(o), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 주문 응답 생성
    private GetOrderResponse makeGetOrderResponse(Order order) {
        return new GetOrderResponse(order.getId(), order.getState().name(), order.getOrderTotal());
    }

    // 주문 취소
    @RequestMapping(path = "/{orderId}/cancel", method = RequestMethod.POST)
    public ResponseEntity<GetOrderResponse> cancel(@PathVariable long orderId) {
        try {
            Order order = orderService.cancel(orderId);
            return new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 주문 수정
    @RequestMapping(path = "/{orderId}/revise", method = RequestMethod.POST)
    public ResponseEntity<GetOrderResponse> revise(@PathVariable long orderId, @RequestBody ReviseOrderRequest request) {
        try {
            Order order = orderService.reviseOrder(orderId, new OrderRevision(Optional.empty(), request.getRevisedLineItemQuantities()));
            return new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
