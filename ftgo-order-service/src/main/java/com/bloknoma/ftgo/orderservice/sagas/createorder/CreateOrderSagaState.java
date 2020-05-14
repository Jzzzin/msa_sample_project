package com.bloknoma.ftgo.orderservice.sagas.createorder;

import com.bloknoma.ftgo.accountservice.api.AuthorizeCommand;
import com.bloknoma.ftgo.consumerservice.api.ValidateOrderByConsumer;
import com.bloknoma.ftgo.kitchenservice.api.*;
import com.bloknoma.ftgo.orderservice.api.events.OrderDetails;
import com.bloknoma.ftgo.orderservice.api.events.OrderLineItem;
import com.bloknoma.ftgo.orderservice.sagaparticipants.ApproveOrderCommand;
import com.bloknoma.ftgo.orderservice.sagaparticipants.RejectOrderCommand;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

// 주문 생성 사가 상태 머신
public class CreateOrderSagaState {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Long orderId;

    private OrderDetails orderDetails;
    private long ticketId;

    private CreateOrderSagaState() {
    }

    public CreateOrderSagaState(Long orderId, OrderDetails orderDetails) {
        this.orderId = orderId;
        this.orderDetails = orderDetails;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    // 티켓 생성 커맨드 이벤트
    CreateTicket makeCreateTicketCommand() {
        return new CreateTicket(getOrderDetails().getRestaurantId(), getOrderId(), makeTicketDetails(getOrderDetails()));
    }

    // 티켓 상세 생성
    private TicketDetails makeTicketDetails(OrderDetails orderDetails) {
        // TODO FIXME
        return new TicketDetails(makeTicketLineItems(orderDetails.getLineItems()));
    }

    // 티켓 목록 생성
    private List<TicketLineItem> makeTicketLineItems(List<OrderLineItem> lineItems) {
        return lineItems.stream().map(this::makeTicketLineItem).collect(toList());
    }

    // 티켓 목록 생성
    private TicketLineItem makeTicketLineItem(OrderLineItem orderLineItem) {
        return new TicketLineItem(orderLineItem.getMenuItemId(), orderLineItem.getName(), orderLineItem.getQuantity());
    }

    // 티켓 생성 응답 처리
    void handleCreateTicketReply(CreateTicketReply reply) {
        logger.debug("getTicketId {}", reply.getTicketId());
        setTicketId(reply.getTicketId());
    }

    // 티켓 생성 취소 커맨드 이벤트
    CancelCreateTicket makeCancelCreateTicketCommand() {
        return new CancelCreateTicket(getOrderId());
    }

    // reject order 커맨드 이벤트
    RejectOrderCommand makeRejectOrderCommand() {
        return new RejectOrderCommand(getOrderId());
    }

    // 주문 검증 커맨드 이벤트
    ValidateOrderByConsumer makeValidateOrderByConsumerCommand() {
        return new ValidateOrderByConsumer(getOrderDetails().getConsumerId(), getOrderId(), getOrderDetails().getOrderTotal());
    }

    // 주문 승인 커맨드 이벤트
    AuthorizeCommand makeAuthorizeCommand() {
        return new AuthorizeCommand(getOrderDetails().getConsumerId(), getOrderId(), getOrderDetails().getOrderTotal());
    }

    // approve order 커맨드 이벤트
    ApproveOrderCommand makeApproveOrderCommand() {
        return new ApproveOrderCommand(getOrderId());
    }

    // 티켓 생성 승인 커맨드 이벤트
    ConfirmCreateTicket makeConfirmCreateTicketCommand() {
        return new ConfirmCreateTicket(getTicketId());
    }
}
