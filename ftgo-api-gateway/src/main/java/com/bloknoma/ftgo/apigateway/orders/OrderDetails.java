package com.bloknoma.ftgo.apigateway.orders;

import com.bloknoma.ftgo.apigateway.proxies.BillInfo;
import com.bloknoma.ftgo.apigateway.proxies.DeliveryInfo;
import com.bloknoma.ftgo.apigateway.proxies.OrderInfo;
import com.bloknoma.ftgo.apigateway.proxies.TicketInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import reactor.util.function.Tuple4;

import java.util.Optional;

// 주문 상세
public class OrderDetails {

    private OrderInfo orderInfo;

    public OrderDetails() {
    }

    public OrderDetails(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public OrderDetails(OrderInfo orderInfo, Optional<TicketInfo> ticketInfo, Optional<DeliveryInfo> deliveryInfo, Optional<BillInfo> billInfo) {
        this(orderInfo);
        System.out.println("FIXME");
    }

    // OrderDetail 생성
    public static OrderDetails makeOrderDetails(Tuple4<OrderInfo, Optional<TicketInfo>, Optional<DeliveryInfo>, Optional<BillInfo>> info) {
        return new OrderDetails(info.getT1(), info.getT2(), info.getT3(), info.getT4());
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
