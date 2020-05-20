package com.bloknoma.ftgo.orderservice.sagas.createorder;

import com.bloknoma.ftgo.accountservice.api.AccountingServiceChannels;
import com.bloknoma.ftgo.accountservice.api.AuthorizeCommand;
import com.bloknoma.ftgo.common.CommonJsonMapperInitializer;
import com.bloknoma.ftgo.consumerservice.api.ConsumerServiceChannels;
import com.bloknoma.ftgo.consumerservice.api.ValidateOrderByConsumer;
import com.bloknoma.ftgo.kitchenservice.api.CancelCreateTicket;
import com.bloknoma.ftgo.kitchenservice.api.ConfirmCreateTicket;
import com.bloknoma.ftgo.kitchenservice.api.CreateTicket;
import com.bloknoma.ftgo.kitchenservice.api.KitchenServiceChannels;
import com.bloknoma.ftgo.orderservice.api.OrderServiceChannels;
import com.bloknoma.ftgo.orderservice.sagaparticipants.*;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.*;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static io.eventuate.tram.sagas.testing.SagaUnitTestSupport.given;

// unit test
public class CreateOrderSagaTest {

    private OrderServiceProxy orderServiceProxy = new OrderServiceProxy();
    private KitchenServiceProxy kitchenServiceProxy = new KitchenServiceProxy();
    private ConsumerServiceProxy consumerServiceProxy = new ConsumerServiceProxy();
    private AccountingServiceProxy accountingServiceProxy = new AccountingServiceProxy();

    @BeforeClass
    public static void initialize() {
        CommonJsonMapperInitializer.registerMoneyModule();
    }

    private CreateOrderSaga makeCreateOrderSaga() {
        return new CreateOrderSaga(orderServiceProxy, consumerServiceProxy, kitchenServiceProxy, accountingServiceProxy);
    }

    // 정상 주문 생성 테스트
    @Test
    public void shouldCreateOrder() {
        given()
            .saga(makeCreateOrderSaga(), new CreateOrderSagaState(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS))
        .expect()
            .command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL))
            .to(ConsumerServiceChannels.consumerServiceChannel)
        .andGiven()
            .successReply()
        .expect()
            .command(new CreateTicket(AJANTA_ID, ORDER_ID, null /* FIXME */))
            .to(KitchenServiceChannels.kitchenServiceChannel)
        .andGiven()
            .successReply()
        .expect()
            .command(new AuthorizeCommand(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL))
            .to(AccountingServiceChannels.accountingServiceChannel)
        .andGiven()
            .successReply()
        .expect()
            .command(new ConfirmCreateTicket(ORDER_ID))
            .to(KitchenServiceChannels.kitchenServiceChannel)
        .andGiven()
            .successReply()
        .expect()
            .command(new ApproveOrderCommand(ORDER_ID))
            .to(OrderServiceChannels.orderServiceChannel);
    }

    // 주문 검증 실패 테스트
    @Test
    public void shouldRejectOrderDutToConsumerVerificationFailed() {
        given()
            .saga(makeCreateOrderSaga(), new CreateOrderSagaState(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS))
        .expect()
            .command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL))
            .to(ConsumerServiceChannels.consumerServiceChannel)
        .andGiven()
            .failureReply()
        .expect()
            .command(new RejectOrderCommand(ORDER_ID))
            .to(OrderServiceChannels.orderServiceChannel);
    }

    // 주문 승인 실패 테스트
    @Test
    public void shouldRejectDueToFailedAuthorization() {
        given()
                .saga(makeCreateOrderSaga(), new CreateOrderSagaState(ORDER_ID, CHICKEN_VINDALOO_ORDER_DETAILS))
        .expect()
                .command(new ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL))
                .to(ConsumerServiceChannels.consumerServiceChannel)
        .andGiven()
                .successReply()
        .expect()
                .command(new CreateTicket(AJANTA_ID, ORDER_ID, null /* FIXME */))
                .to(KitchenServiceChannels.kitchenServiceChannel)
        .andGiven()
                .successReply()
        .expect()
                .command(new AuthorizeCommand(CONSUMER_ID, ORDER_ID, CHICKEN_VINDALOO_ORDER_TOTAL))
                .to(AccountingServiceChannels.accountingServiceChannel)
        .andGiven()
                .failureReply()
        .expect()
                .command(new CancelCreateTicket(ORDER_ID))
                .to(KitchenServiceChannels.kitchenServiceChannel)
        .andGiven()
                .successReply()
        .expect()
                .command(new RejectOrderCommand(ORDER_ID))
                .to(OrderServiceChannels.orderServiceChannel);
    }
}
