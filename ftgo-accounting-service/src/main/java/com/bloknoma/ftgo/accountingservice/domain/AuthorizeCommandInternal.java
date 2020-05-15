package com.bloknoma.ftgo.accountingservice.domain;

import com.bloknoma.ftgo.common.Money;
import io.eventuate.tram.commands.common.Command;

// 승인 커맨드 내부
public class AuthorizeCommandInternal implements AccountCommand, Command {

    private String consumerId;
    private String orderId;
    private Money orderTotal;

    private AuthorizeCommandInternal() {
    }

    public AuthorizeCommandInternal(String consumerId, String orderId, Money orderTotal) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
        this.orderTotal = orderTotal;
    }
}
