package com.bloknoma.ftgo.accountservice.api;

import com.bloknoma.ftgo.common.Money;
import io.eventuate.tram.commands.common.Command;

// 주문 승인 커맨드
public class AuthorizeCommand implements Command {
    private long consumerId;
    private Long orderId;
    private Money orderTotal;

    private Money amount;

    private AuthorizeCommand() {
    }

    public AuthorizeCommand(long consumerId, Long orderId, Money orderTotal) {
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

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }
}
