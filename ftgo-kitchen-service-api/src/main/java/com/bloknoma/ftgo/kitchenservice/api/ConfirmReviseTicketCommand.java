package com.bloknoma.ftgo.kitchenservice.api;

import io.eventuate.tram.commands.common.Command;

import java.util.Map;

// 티켓 수정 승인 커맨드?
public class ConfirmReviseTicketCommand implements Command {
    private long restaurantId;
    private long orderId;
    private Map<String, Integer> revisedLineItemQuantities;

    private ConfirmReviseTicketCommand() {
    }

    public ConfirmReviseTicketCommand(long restaurantId, long orderId, Map<String, Integer> revisedLineItemQuantities) {
        this.restaurantId = restaurantId;
        this.orderId = orderId;
        this.revisedLineItemQuantities = revisedLineItemQuantities;
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

    public Map<String, Integer> getRevisedLineItemQuantities() {
        return revisedLineItemQuantities;
    }

    public void setRevisedLineItemQuantities(Map<String, Integer> revisedLineItemQuantities) {
        this.revisedLineItemQuantities = revisedLineItemQuantities;
    }
}
