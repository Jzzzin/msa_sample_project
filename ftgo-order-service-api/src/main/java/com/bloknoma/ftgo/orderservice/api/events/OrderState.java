package com.bloknoma.ftgo.orderservice.api.events;

// 주문 상태 정보
public enum OrderState {
    APPROVAL_PENDING,
    APPROVED,
    REJECTED,
    CANCEL_PENDING,
    CANCELLED,
    REVISION_PENDING,
}
