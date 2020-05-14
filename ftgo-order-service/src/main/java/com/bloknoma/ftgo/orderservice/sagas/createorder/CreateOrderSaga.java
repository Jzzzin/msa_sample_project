package com.bloknoma.ftgo.orderservice.sagas.createorder;

import com.bloknoma.ftgo.kitchenservice.api.CreateTicketReply;
import com.bloknoma.ftgo.orderservice.sagaparticipants.AccountingServiceProxy;
import com.bloknoma.ftgo.orderservice.sagaparticipants.ConsumerServiceProxy;
import com.bloknoma.ftgo.orderservice.sagaparticipants.KitchenServiceProxy;
import com.bloknoma.ftgo.orderservice.sagaparticipants.OrderServiceProxy;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 주문 생성 사가
public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaState> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SagaDefinition<CreateOrderSagaState> sagaDefinition;

    public CreateOrderSaga(OrderServiceProxy orderService,
                           ConsumerServiceProxy consumerService,
                           KitchenServiceProxy kitchenService,
                           AccountingServiceProxy accountingService) {
        this.sagaDefinition =
                step()
                    .withCompensation(orderService.reject, CreateOrderSagaState::makeRejectOrderCommand)
                .step()
                    .invokeParticipant(consumerService.validateOrder, CreateOrderSagaState::makeValidateOrderByConsumerCommand)
                .step()
                    .invokeParticipant(kitchenService.create, CreateOrderSagaState::makeCreateTicketCommand)
                    .onReply(CreateTicketReply.class, CreateOrderSagaState::handleCreateTicketReply)
                    .withCompensation(kitchenService.cancel, CreateOrderSagaState::makeCancelCreateTicketCommand)
                .step()
                    .invokeParticipant(accountingService.authorize, CreateOrderSagaState::makeAuthorizeCommand)
                .step()
                    .invokeParticipant(kitchenService.confirmCreate, CreateOrderSagaState::makeConfirmCreateTicketCommand)
                .step()
                    .invokeParticipant(orderService.approve, CreateOrderSagaState::makeApproveOrderCommand)
                .build();

    }

    @Override
    public SagaDefinition<CreateOrderSagaState> getSagaDefinition() {
        return sagaDefinition;
    }
}
