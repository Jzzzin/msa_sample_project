package com.bloknoma.ftgo.orderservice.domain.exception;

// 주문 조회 실패 예외 처리
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("Order not found " + orderId);
    }
}
