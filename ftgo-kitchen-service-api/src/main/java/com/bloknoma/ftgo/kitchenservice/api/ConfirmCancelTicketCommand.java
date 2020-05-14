package com.bloknoma.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

// 티켓 취소 승인 커맨드?
public class ConfirmCancelTicketCommand implements Command {

    private long restaurantId;
    private long orderId;

    private ConfirmCancelTicketCommand() {
    }

    public ConfirmCancelTicketCommand(long restaurantId, long orderId) {
        this.restaurantId = restaurantId;
        this.orderId = orderId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
