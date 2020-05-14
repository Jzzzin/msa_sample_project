package com.bloknoma.ftgo.orderservice.domain.exception;

// 메뉴 목록 조회 실패 예외 처리
public class InvalidMenuItemIdException extends RuntimeException {
    public InvalidMenuItemIdException(String menuItemId) {
        super("Invalid menu item id " + menuItemId);
    }
}
