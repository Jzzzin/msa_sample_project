package com.bloknoma.ftgo.accountservice.api;

import com.bloknoma.ftgo.common.Money;
import io.eventuate.tram.commands.common.Command;

// 주문 취소 승인 커맨드
public class ReverseAuthorizationCommand implements Command {
    private long consumerId;
    private Long orderId;
    private Money orderTotal;

    private ReverseAuthorizationCommand() {
    }

    public ReverseAuthorizationCommand(long consumerId, Long orderId, Money orderTotal) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
        this.orderTotal = orderTotal;
    }
}
