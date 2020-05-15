package com.bloknoma.ftgo.kitchenservice.domain;

// 티켓 취소 커맨드
public class CancelCommand {
    private long orderId;
    private boolean force;

    public long getOrderId() {
        return orderId;
    }

    public boolean isForce() {
        return force;
    }
}
