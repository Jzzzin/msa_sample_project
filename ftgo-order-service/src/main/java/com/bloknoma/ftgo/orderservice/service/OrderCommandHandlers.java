package com.bloknoma.ftgo.orderservice.service;

import com.bloknoma.ftgo.common.UnsupportedStateTransitionException;
import com.bloknoma.ftgo.orderservice.domain.OrderService;
import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;
import com.bloknoma.ftgo.orderservice.sagaparticipants.*;
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

// 주문 커맨드 핸들러
public class OrderCommandHandlers {

    @Autowired
    private OrderService orderService;

    // 커맨드 메시지 라우팅
    public CommandHandlers commandHandler() {
        return SagaCommandHandlersBuilder
                .fromChannel("orderService")
                .onMessage(ApproveOrderCommand.class, this::approveOrder)
                .onMessage(RejectOrderCommand.class, this::rejectOrder)

                .onMessage(BeginCancelCommand.class, this::beginCancel)
                .onMessage(UndoBeginCancelCommand.class, this::undoCancel)
                .onMessage(ConfirmCancelOrderCommand.class, this::confirmCancel)

                .onMessage(BeginReviseOrderCommand.class, this::beginReviseOrder)
                .onMessage(UndoBeginReviseOrderCommand.class, this::undoPendingRevision)
                .onMessage(ConfirmReviseOrderCommand.class, this::confirmRevision)
                .build();
    }

    // 주문 승인
    public Message approveOrder(CommandMessage<ApproveOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.approveOrder(orderId);
        return withSuccess();
    }

    // 주문 거절
    public Message rejectOrder(CommandMessage<RejectOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.rejectOrder(orderId);
        return withSuccess();
    }

    // 주문 취소
    public Message beginCancel(CommandMessage<BeginCancelCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        try {
            orderService.beginCancel(orderId);
            return withSuccess();
        } catch (UnsupportedStateTransitionException e) {
            return withFailure();
        }
    }

    // 주문 취소 언두
    public Message undoCancel(CommandMessage<UndoBeginCancelCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.undoCancel(orderId);
        return withSuccess();
    }

    // 주문 취소 확인
    public Message confirmCancel(CommandMessage<ConfirmCancelOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.confirmCancelled(orderId);
        return withSuccess();
    }

    // 주문 수정
    public Message beginReviseOrder(CommandMessage<BeginReviseOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        OrderRevision revision = cm.getCommand().getRevision();
        try {
            return orderService.beginReviseOrder(orderId, revision).map(result -> withSuccess(new BeginReviseOrderReply(result.getChange().getNewOrderTotal()))).orElseGet(CommandHandlerReplyBuilder::withFailure);
        } catch (UnsupportedStateTransitionException e) {
            return withFailure();
        }
    }

    // 주문 수정 언두
    public Message undoPendingRevision(CommandMessage<UndoBeginReviseOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.undoPendingRevision(orderId);
        return withSuccess();
    }

    // 주문 수정 확인
    public Message confirmRevision(CommandMessage<ConfirmReviseOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        OrderRevision revision = cm.getCommand().getRevision();
        orderService.confirmRevision(orderId, revision);
        return withSuccess();
    }
}
