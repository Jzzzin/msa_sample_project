package com.bloknoma.ftgo.orderservice.domain.exception;

// 레스토랑 조회 실패 예외 처리
public class RestaurantNotFoundException extends RuntimeException {
    public RestaurantNotFoundException(long restaurantId) {
        super("Restaurant not found with id " + restaurantId);
    }
}
