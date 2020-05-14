package com.bloknoma.ftgo.orderservice.sagaparticipants;

import io.eventuate.tram.commands.common.Command;

// order command
public abstract class OrderCommand implements Command {

    private long orderId;

    protected OrderCommand() {
    }

    protected OrderCommand(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
