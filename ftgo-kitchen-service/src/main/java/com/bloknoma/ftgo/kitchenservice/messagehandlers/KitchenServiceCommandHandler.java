package com.bloknoma.ftgo.kitchenservice.messagehandlers;

import com.bloknoma.ftgo.kitchenservice.api.*;
import com.bloknoma.ftgo.kitchenservice.domain.KitchenService;
import com.bloknoma.ftgo.kitchenservice.domain.Ticket;
import com.bloknoma.ftgo.kitchenservice.domain.exception.RestaurantDetailsVerificationException;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;
import static io.eventuate.tram.sagas.participant.SagaReplyMessageBuilder.withLock;

// 티켓 커맨드 핸들러
public class KitchenServiceCommandHandler {

    @Autowired
    private KitchenService kitchenService;

    // 커맨드 메시지 라우팅
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(KitchenServiceChannels.kitchenServiceChannel)
                .onMessage(CreateTicket.class, this::createTicket)
                .onMessage(ConfirmCreateTicket.class, this::confirmCreateTicket)
                .onMessage(CancelCreateTicket.class, this::cancelCreateTicket)

                .onMessage(BeginCancelTicketCommand.class, this::beginCancelTicket)
                .onMessage(ConfirmCancelTicketCommand.class, this::confirmCancelTicket)
                .onMessage(UndoBeginCancelTicketCommand.class, this::undoBeginCancelTicket)

                .onMessage(BeginReviseTicketCommand.class, this::beginReviseTicket)
                .onMessage(UndoBeginReviseTicketCommand.class, this::undoBeginReviseTicket)
                .onMessage(ConfirmReviseTicketCommand.class, this::confirmReviseTicket)
                .build();
    }

    // 티켓 생성
    private Message createTicket(CommandMessage<CreateTicket> cm) {
        CreateTicket command = cm.getCommand();
        long restaurantId = command.getRestaurantId();
        Long ticketId = command.getOrderId();
        TicketDetails ticketDetails = command.getTicketDetails();

        try {
            Ticket ticket = kitchenService.createTicket(restaurantId, ticketId, ticketDetails);
            CreateTicketReply reply = new CreateTicketReply(ticket.getId());
            return withLock(Ticket.class, ticket.getId()).withSuccess(reply);
        } catch (RestaurantDetailsVerificationException e) {
            return withFailure();
        }
    }

    // 티켓 생성 확인
    private Message confirmCreateTicket(CommandMessage<ConfirmCreateTicket> cm) {
        Long ticketId = cm.getCommand().getTicketId();
        kitchenService.confirmCreateTicket(ticketId);
        return withSuccess();
    }

    // 티켓 생성 취소
    private Message cancelCreateTicket(CommandMessage<CancelCreateTicket> cm) {
        Long ticketId = cm.getCommand().getTicketId();
        kitchenService.cancelCreateTicket(ticketId);
        return withSuccess();
    }

    // 티켓 취소
    private Message beginCancelTicket(CommandMessage<BeginCancelTicketCommand> cm) {
        kitchenService.cancelTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
        return withSuccess();
    }

    // 티켓 취소 확인
    private Message confirmCancelTicket(CommandMessage<ConfirmCancelTicketCommand> cm) {
        kitchenService.confirmCancelTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
        return withSuccess();
    }

    // 티켓 취소 언두
    private Message undoBeginCancelTicket(CommandMessage<UndoBeginCancelTicketCommand> cm) {
        kitchenService.undoCancel(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
        return withSuccess();
    }

    // 티켓 수정
    public Message beginReviseTicket(CommandMessage<BeginReviseTicketCommand> cm) {
        kitchenService.beginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedLineItemQuantities());
        return withSuccess();
    }

    // 티켓 수정 언두
    public Message undoBeginReviseTicket(CommandMessage<UndoBeginReviseTicketCommand> cm) {
        kitchenService.undoBeginReviseOrder(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId());
        return withSuccess();
    }

    // 티켓 수정 확인
    public Message confirmReviseTicket(CommandMessage<ConfirmReviseTicketCommand> cm) {
        kitchenService.confirmReviseTicket(cm.getCommand().getRestaurantId(), cm.getCommand().getOrderId(), cm.getCommand().getRevisedLineItemQuantities());
        return withSuccess();
    }


}
