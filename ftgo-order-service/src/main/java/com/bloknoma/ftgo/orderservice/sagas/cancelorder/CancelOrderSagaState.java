package com.bloknoma.ftgo.orderservice.sagas.cancelorder;

// 주문 취소 사가 상태
public enum CancelOrderSagaState {
    state, WAITING_TO_AUTHORIZE, COMPLETED, REVERSING
}
