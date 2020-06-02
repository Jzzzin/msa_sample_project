package com.bloknoma.ftgo.orderservice.cucumber;

import com.bloknoma.ftgo.accountservice.api.AuthorizeCommand;
import com.bloknoma.ftgo.common.CommonJsonMapperInitializer;
import com.bloknoma.ftgo.consumerservice.api.ValidateOrderByConsumer;
import com.bloknoma.ftgo.kitchenservice.api.CancelCreateTicket;
import com.bloknoma.ftgo.kitchenservice.api.ConfirmCreateTicket;
import com.bloknoma.ftgo.kitchenservice.api.CreateTicket;
import com.bloknoma.ftgo.kitchenservice.api.CreateTicketReply;
import com.bloknoma.ftgo.orderservice.OrderDetailsMother;
import com.bloknoma.ftgo.orderservice.RestaurantMother;
import com.bloknoma.ftgo.orderservice.api.web.CreateOrderRequest;
import com.bloknoma.ftgo.orderservice.domain.Order;
import com.bloknoma.ftgo.orderservice.domain.repository.RestaurantRepository;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantCreated;
import com.bloknoma.ftgo.testutil.FtgoTestUtil;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.jdbckafka.TramJdbcKafkaConfiguration;
import io.eventuate.tram.messaging.consumer.MessageConsumer;
import io.eventuate.tram.sagas.testing.SagaParticipantChannels;
import io.eventuate.tram.sagas.testing.SagaParticipantStubManager;
import io.eventuate.tram.sagas.testing.SagaParticipantStubManagerConfiguration;
import io.eventuate.tram.testing.MessageTracker;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT_MENU;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;
import static io.eventuate.util.test.async.Eventually.eventually;
import static io.restassured.RestAssured.given;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


@SpringBootTest(classes = OrderServiceComponentTestStepDefinitions.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration
public class OrderServiceComponentTestStepDefinitions {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Response response;
    private long consumerId;

    static {
        CommonJsonMapperInitializer.registerMoneyModule();
    }

    private int port = 8082;
    private String host =FtgoTestUtil.getDockerHostIp();

    protected String baseUrl(String path) {
        return String.format("http://%s:%s%s", host, port, path);
    }

    @Configuration
    @EnableAutoConfiguration
    @Import({TramJdbcKafkaConfiguration.class, SagaParticipantStubManagerConfiguration.class})
    @EnableJpaRepositories(basePackageClasses = RestaurantRepository.class)
    // Need to verify that restaurant has been created. Replace with verifyRestaurantCreated In OrderService
    @EntityScan(basePackageClasses = Order.class)
    public static class TestConfiguration {

        @Bean
        public SagaParticipantChannels sagaParticipantChannels() {
            return new SagaParticipantChannels("consumerService", "kitchenService", "accountingService", "orderService");
        }

        @Bean
        public MessageTracker messageTracker(MessageConsumer messageConsumer) {
            return new MessageTracker(singleton("com.bloknoma.ftgo.orderservice.domain.Order"), messageConsumer);
        }
    }

    @Autowired
    protected SagaParticipantStubManager sagaParticipantStubManager;

    @Autowired
    protected MessageTracker messageTracker;

    @Autowired
    protected DomainEventPublisher domainEventPublisher;

    @Autowired
    protected RestaurantRepository restaurantRepository;

    @Before
    public void setUp() {
        sagaParticipantStubManager.reset();
    }

    // feature definition
    @Given("A valid consumer")
    public void useConsumer() {
        // 스텁 설정
        sagaParticipantStubManager.
                forChannel("consumerService")
                .when(ValidateOrderByConsumer.class).replyWith(cm -> withSuccess());
    }

    public enum CreditCardType {
        valid, expired
    }

    // feature definition
    @Given("using a(.?) (.*) credit card")
    public void useCreditCard(String ignore, CreditCardType creditCard) {
        // 스텁 설정
        switch (creditCard) {
            case valid:
                sagaParticipantStubManager
                        .forChannel("accountingService")
                        .when(AuthorizeCommand.class).replyWithSuccess();
                break;
            case expired:
                sagaParticipantStubManager
                        .forChannel("accountingService")
                        .when(AuthorizeCommand.class).replyWithFailure();
                break;
            default:
                fail("Don't know what to do with this credit card");
        }
    }

    // feature definition
    @Given("the restaurant is accepting orders")
    public void restaurantAcceptsOrder() {
        // 스텁 설정
        sagaParticipantStubManager
                .forChannel("kitchenService")
                .when(CreateTicket.class).replyWith(cm -> withSuccess(new CreateTicketReply(cm.getCommand().getOrderId())))
                .when(ConfirmCreateTicket.class).replyWithSuccess()
                .when(CancelCreateTicket.class).replyWithSuccess();

        // 레스토랑 확인
        if (!restaurantRepository.findById(RestaurantMother.AJANTA_ID).isPresent()) {
            domainEventPublisher.publish("com.bloknoma.ftgo.restaurantservice.domain.Restaurant", RestaurantMother.AJANTA_ID,
                    Collections.singletonList(new RestaurantCreated(RestaurantMother.AJANTA_RESTAURANT_NAME, AJANTA_RESTAURANT_MENU)));

            eventually(() -> {
                FtgoTestUtil.assertPresent(restaurantRepository.findById(RestaurantMother.AJANTA_ID));
            });
        }
    }

    // feature definition
    @When("I place an order for Chicken Vindaloo at Ajanta")
    public void placeOrder() {

        // 주문 생성
        response = given()
                .body(new CreateOrderRequest(consumerId,
                        RestaurantMother.AJANTA_ID,
                        Collections.singletonList(new CreateOrderRequest.LineItem(RestaurantMother.CHICKEN_VINDALOO_MENU_ITEM_ID,
                                OrderDetailsMother.CHICKEN_VINDALOO_QUANTITY))))
                .contentType("application/json")
                .when()
                .post(baseUrl("/orders"));
    }

    // feature definition
    @Then("the order should be (.*)")
    public void theOrderShouldBeInState(String desiredOrderState) {
        // TODO This doesn't make sense when the 'OrderService' is live => duplicate replies

//        sagaParticipantStubManager
//                .forChannel("orderService")
//                .when(ApproveOrderCommand.class).replyWithSuccess();

        // order ID 추출
        Integer orderId = this.response
                .then()
                .statusCode(200)
                .extract()
                .path("orderId");

        assertNotNull(orderId);

        eventually(() -> {
            // order state 추출
            String state = given()
                    .when()
                    .get(baseUrl("/orders/" + orderId))
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("state");
            assertEquals(desiredOrderState, state);
        });

        sagaParticipantStubManager.verifyCommandReceived("kitchenService", CreateTicket.class);
    }

    // feature definition
    @And("an (.*) event should be published")
    public void verifyEventPublished(String expectedEventClass) {
        messageTracker.assertDomainEventPublished("com.bloknoma.ftgo.orderservice.domain.Order",
                findEventClass(expectedEventClass, "com.bloknoma.ftgo.orderservice.domain.event", "com.bloknoma.ftgo.orderservice.api.events"));
    }

    private String findEventClass(String expectedEventClass, String... packages) {
        return Arrays.stream(packages).map(p -> p + "." + expectedEventClass).filter(className -> {
            try {
                Class.forName(className);
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }).findFirst().orElseThrow(() -> new RuntimeException(String.format("Cannot find class %s in packages %s", expectedEventClass, String.join(",", packages))));
    }

}
