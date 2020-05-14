package com.bloknoma.ftgo.orderservice.sagas.cancelorder;

import com.bloknoma.ftgo.accountservice.api.AccountingServiceChannels;
import com.bloknoma.ftgo.accountservice.api.ReverseAuthorizationCommand;
import com.bloknoma.ftgo.kitchenservice.api.BeginCancelTicketCommand;
import com.bloknoma.ftgo.kitchenservice.api.ConfirmCancelTicketCommand;
import com.bloknoma.ftgo.kitchenservice.api.KitchenServiceChannels;
import com.bloknoma.ftgo.kitchenservice.api.UndoBeginCancelTicketCommand;
import com.bloknoma.ftgo.orderservice.api.OrderServiceChannels;
import com.bloknoma.ftgo.orderservice.sagaparticipants.BeginCancelCommand;
import com.bloknoma.ftgo.orderservice.sagaparticipants.ConfirmCancelOrderCommand;
import com.bloknoma.ftgo.orderservice.sagaparticipants.UndoBeginCancelCommand;
import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

// 주문 취소 사가
public class CancelOrderSaga implements SimpleSaga<CancelOrderSagaData> {

    private SagaDefinition<CancelOrderSagaData> sagaDefinition;

    @PostConstruct
    public void initializeSagaDefinition() {
        sagaDefinition = step()
                    .invokeParticipant(this::beginCancel)
                    .withCompensation(this::undoBeginCancel)
                .step()
                    .invokeParticipant(this::beginCancelTicket)
                    .withCompensation(this::undoBeginCancelTicket)
                .step()
                    .invokeParticipant(this::reverseAuthorization)
                .step()
                    .invokeParticipant(this::confirmTicketCancel)
                .step()
                    .invokeParticipant(this::confirmOrderCancel)
                .build();
    }

    // 주문 취소 확인 커맨드
    private CommandWithDestination confirmOrderCancel(CancelOrderSagaData data) {
        return send(new ConfirmCancelOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.orderServiceChannel)
                .build();
    }

    // 티켓 취소 확인 커맨드
    private CommandWithDestination confirmTicketCancel(CancelOrderSagaData data) {
        return send(new ConfirmCancelTicketCommand(data.getRestaurantId(), data.getOrderId()))
                .to(KitchenServiceChannels.kitchenServiceChannel)
                .build();
    }

    // 주문 취소 승인 커맨드
    private CommandWithDestination reverseAuthorization(CancelOrderSagaData data) {
        return send(new ReverseAuthorizationCommand(data.getConsumerId(), data.getOrderId(), data.getOrderTotal()))
                .to(AccountingServiceChannels.accountingServiceChannel)
                .build();
    }

    // 티켓 취소 언두 커맨드
    private CommandWithDestination undoBeginCancelTicket(CancelOrderSagaData data) {
        return send(new UndoBeginCancelTicketCommand(data.getRestaurantId(), data.getOrderId()))
                .to(KitchenServiceChannels.kitchenServiceChannel)
                .build();
    }

    // 티켓 취소 시작 커맨드
    private CommandWithDestination beginCancelTicket(CancelOrderSagaData data) {
        return send(new BeginCancelTicketCommand(data.getRestaurantId(), (long) data.getOrderId()))
                .to(KitchenServiceChannels.kitchenServiceChannel)
                .build();
    }

    // 주문 취소 언두 커맨드
    private CommandWithDestination undoBeginCancel(CancelOrderSagaData data) {
        return send(new UndoBeginCancelCommand(data.getOrderId()))
                .to(OrderServiceChannels.orderServiceChannel)
                .build();
    }

    // 주문 취소 시작 커맨드
    private CommandWithDestination beginCancel(CancelOrderSagaData data) {
        return send(new BeginCancelCommand(data.getOrderId()))
                .to(OrderServiceChannels.orderServiceChannel)
                .build();
    }

    @Override
    public SagaDefinition<CancelOrderSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition);
        return sagaDefinition;
    }
}
