package com.bloknoma.ftgo.kitchenservice.api;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

// 티켓 목록 정보
public class TicketDetails {
    private List<TicketLineItem> lineItems;

    public TicketDetails() {
    }

    public TicketDetails(List<TicketLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public List<TicketLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<TicketLineItem> lineItems) {
        this.lineItems = lineItems;
    }
}
