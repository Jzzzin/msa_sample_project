package com.bloknoma.ftgo.accountservice.api;

import com.bloknoma.ftgo.common.Money;
import io.eventuate.tram.commands.common.Command;

// 주문 수정 승인
public class ReviseAuthorization implements Command {
    private long consumerId;
    private Long orderid;
    private Money orderTotal;

    private ReviseAuthorization() {
    }

    public ReviseAuthorization(long consumerId, Long orderid, Money orderTotal) {
        this.consumerId = consumerId;
        this.orderid = orderid;
        this.orderTotal = orderTotal;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
        this.orderTotal = orderTotal;
    }
}
