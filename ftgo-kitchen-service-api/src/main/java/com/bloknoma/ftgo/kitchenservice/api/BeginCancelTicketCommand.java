package com.bloknoma.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

// 티켓 취소 시작 커맨드?
public class BeginCancelTicketCommand implements Command {
    private long restaurantId;
    private long orderId;

    private BeginCancelTicketCommand() {
    }

    public BeginCancelTicketCommand(long restaurantId, long orderId) {
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
