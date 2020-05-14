package com.bloknoma.ftgo.orderservice.sagas.reviseorder;

import com.bloknoma.ftgo.accountservice.api.AccountingServiceChannels;
import com.bloknoma.ftgo.accountservice.api.ReviseAuthorization;
import com.bloknoma.ftgo.kitchenservice.api.BeginReviseTicketCommand;
import com.bloknoma.ftgo.kitchenservice.api.ConfirmReviseTicketCommand;
import com.bloknoma.ftgo.kitchenservice.api.KitchenServiceChannels;
import com.bloknoma.ftgo.kitchenservice.api.UndoBeginReviseTicketCommand;
import com.bloknoma.ftgo.orderservice.api.OrderServiceChannels;
import com.bloknoma.ftgo.orderservice.sagaparticipants.BeginReviseOrderCommand;
import com.bloknoma.ftgo.orderservice.sagaparticipants.BeginRiviseOrderReply;
import com.bloknoma.ftgo.orderservice.sagaparticipants.ConfirmReviseOrderCommand;
import com.bloknoma.ftgo.orderservice.sagaparticipants.UndoBeginReviseOrderCommand;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

// 주문 수정 사가
public class ReviseOrderSaga implements SimpleSaga<ReviseOrderSagaData> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SagaDefinition<ReviseOrderSagaData> sagaDefinition;

    @PostConstruct
    public void initializeSagaDefinition() {
        sagaDefinition = step()
                    .invokeParticipant(this::beginReviseOrder)
                    .onReply(BeginRiviseOrderReply.class, this::handleBeginReviseOrderReply)
                    .withCompensation(this::undoBeginReviseOrder)
                .step()
                    .invokeParticipant(this::beginReviseTicket)
                    .withCompensation(this::undoBeginReviseTicket)
                .step()
                    .invokeParticipant(this::reviseAuthorization)
                .step()
                  .invokeParticipant(this::confirmTicketRevision)
                .step()
                  .invokeParticipant(this::confirmOrderRevision)
                .build();
    }

    // 주문 수정 확인 커맨드
    private CommandWithDestination confirmOrderRevision(ReviseOrderSagaData data) {
        return send(new ConfirmReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
                .to(OrderServiceChannels.orderServiceChannel)
                .build();
    }

    // 티켓 수정 확인 커맨드
    private CommandWithDestination confirmTicketRevision(ReviseOrderSagaData data) {
        return send(new ConfirmReviseTicketCommand(data.getRestaurantId(), data.getOrderId(), data.getOrderRevision().getRevisedLineItemQuantities()))
                .to(KitchenServiceChannels.kitchenServiceChannel)
                .build();
    }

    // 주문 수정 승인 커맨드
    private CommandWithDestination reviseAuthorization(ReviseOrderSagaData data) {
        return send(new ReviseAuthorization(data.getConsumerId(), data.getOrderId(), data.getRevisedOrderTotal()))
                .to(AccountingServiceChannels.accountingServiceChannel)
                .build();
    }

    // 티켓 수정 언두 커맨드
    private CommandWithDestination undoBeginReviseTicket(ReviseOrderSagaData data) {
        return send(new UndoBeginReviseTicketCommand(data.getRestaurantId(), data.getOrderId()))
                .to(KitchenServiceChannels.kitchenServiceChannel)
                .build();
    }

    // 티켓 수정 시작 커맨드
    private CommandWithDestination beginReviseTicket(ReviseOrderSagaData data) {
        return send(new BeginReviseTicketCommand(data.getRestaurantId(), data.getOrderId(), data.getOrderRevision().getRevisedLineItemQuantities()))
                .to(KitchenServiceChannels.kitchenServiceChannel)
                .build();
    }

    // 주문 수정 언두 커맨드
    private CommandWithDestination undoBeginReviseOrder(ReviseOrderSagaData data) {
        return send(new UndoBeginReviseOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.orderServiceChannel)
                .build();
    }

    // 주문 수정 응답 처리
    private void handleBeginReviseOrderReply(ReviseOrderSagaData data, BeginRiviseOrderReply reply) {
        logger.info("ƒ order total: {}", reply.getRevisedOrderTotal());
        data.setRevisedOrderTotal(reply.getRevisedOrderTotal());
    }

    // 주문 수정 시작 커맨드
    private CommandWithDestination beginReviseOrder(ReviseOrderSagaData data) {
        return send(new BeginReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
                .to(OrderServiceChannels.orderServiceChannel)
                .build();
    }

    @Override
    public SagaDefinition<ReviseOrderSagaData> getSagaDefinition() {
        return sagaDefinition;
    }
}
