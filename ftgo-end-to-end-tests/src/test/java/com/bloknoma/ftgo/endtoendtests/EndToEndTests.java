package com.bloknoma.ftgo.endtoendtests;

import com.bloknoma.ftgo.common.CommonJsonMapperInitializer;
import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.common.PersonName;
import com.bloknoma.ftgo.consumerservice.api.web.CreateConsumerRequest;
import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import com.bloknoma.ftgo.orderservice.api.web.CreateOrderRequest;
import com.bloknoma.ftgo.orderservice.api.web.ReviseOrderRequest;
import com.bloknoma.ftgo.restaurantservice.events.CreateRestaurantRequest;
import com.bloknoma.ftgo.restaurantservice.events.MenuItem;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;
import com.bloknoma.ftgo.testutil.FtgoTestUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.util.test.async.Eventually;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// End to End test
public class EndToEndTests {

    // TODO Move to a shared module

    public static final String CHICKEN_VINDALOO_MENU_ITEM_ID = "1";
    public static final String RESTAURANT_NAME = "My Restaurant";

    private final int revisedQuantityOfChickenVindaloo = 10;
    private String host = FtgoTestUtil.getDockerHostIp();
    private int consumerId;
    private int restaurantId;
    private int orderId;
    private final Money priceOfChickendVindaloo = new Money("12.34");

    private String baseUrl(int port, String path, String... pathElements) {
        StringBuilder sb = new StringBuilder("http://");
        sb.append(host);
        sb.append(":");
        sb.append(port);
        sb.append("/");
        sb.append(path);

        for (String pe : pathElements) {
            sb.append("/");
            sb.append(pe);
        }
        String s = sb.toString();
        System.out.println("url=" + s);
        return s;
    }

    private int consumerPort = 8081;
    private int orderPort = 8082;
    private int accountingPort = 8085;
    private int restaurantsPort = 8084;
    private int kitchenPort = 8083;
    private int apiGatewayPort = 8087;

    private String consumerBaseUrl(String... pathElements) {
        return baseUrl(consumerPort, "consumers", pathElements);
    }

    private String accountingBaseUrl(String... pathElements) {
        return baseUrl(accountingPort, "accounts", pathElements);
    }

    private String restaurantBaseUrl(String... pathElements) {
        return baseUrl(restaurantsPort, "restaurants", pathElements);
    }

    private String kitchenRestaurantBaseUrl(String... pathElements) {
        return baseUrl(kitchenPort, "restaurants", pathElements);
    }

    private String orderBaseUrl(String... pathElements) {
        return baseUrl(apiGatewayPort, "orders", pathElements);
    }

    private String orderRestaurantBaseUrl(String... pathElements) {
        return baseUrl(orderPort, "restaurants", pathElements);
    }

    private String orderHistoryBaseUrl(String... pathElements) {
        return baseUrl(apiGatewayPort, "orders", pathElements);
    }

    @BeforeClass
    public static void initialize() {
        CommonJsonMapperInitializer.registerMoneyModule();

        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (aClass, s) -> JSonMapper.objectMapper
        ));
    }

    @Test
    public void shouldCreateOrder() {

        createOrder();

        reviseOrder();

        cancelOrder();
    }

    private void reviseOrder() {
        reviseOrder(orderId);
        verifyOrderRevised(orderId);
    }

    private void verifyOrderRevised(int orderId) {
        // 주문 총액 확인
        Eventually.eventually(String.format("verifyOrderRevised state %s", orderId), () -> {
            String orderTotal = given()
                    .when()
                    .get(baseUrl(orderPort, "orders", Integer.toString(orderId)))
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("orderTotal");
            assertEquals(priceOfChickendVindaloo.multiply(revisedQuantityOfChickenVindaloo).asString(), orderTotal);
        });
        // 주문 상태 확인
        Eventually.eventually(String.format("verifyOrderRevised state %s", orderId), () -> {
            String state = given()
                    .when()
                    .get(orderBaseUrl(Integer.toString(orderId)))
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("orderInfo.state");
            assertEquals("APPROVED", state);
        });
    }

    private void reviseOrder(int orderId) {
        given()
                .body(new ReviseOrderRequest(Collections.singletonMap(CHICKEN_VINDALOO_MENU_ITEM_ID, revisedQuantityOfChickenVindaloo)))
                .contentType("application/json")
                .when()
                .post(orderBaseUrl(Integer.toString(orderId), "revise"))
                .then()
                .statusCode(200);
    }

    private void cancelOrder() {
        cancelOrder(orderId);

        verifyOrderCancelled(orderId);

        verifyOrderHistoryUpdated(orderId, consumerId, OrderState.CANCELLED.name());
    }

    private void verifyOrderCancelled(int orderId) {
        // 주문 상태 확인
        Eventually.eventually(String.format("verifyOrderCancelled %s", orderId), () -> {
            String state = given()
                    .when()
                    .get(orderBaseUrl(Integer.toString(orderId)))
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("orderInfo.state");
            assertEquals("CANCELLED", state);
        });
    }

    private void cancelOrder(int orderId) {
        given()
                .body("{}")
                .contentType("application/json")
                .when()
                .post(orderBaseUrl(Integer.toString(orderId), "cancel"))
                .then()
                .statusCode(200);
    }

    private void createOrder() {
        consumerId = createConsumer();

        verifyAccountCreatedForConsumer(consumerId);

        restaurantId = createRestaurant();

        verifyRestaurantCreatedInKitchenService(restaurantId);

        verifyRestaurantCreatedInOrderService(restaurantId);

        orderId = createOrder(consumerId, restaurantId);

        verifyOrderAuthorized(orderId);

        verifyOrderHistoryUpdated(orderId, consumerId, OrderState.APPROVED.name());
    }

    private Integer createConsumer() {
        Integer consumerId = given()
                .body(new CreateConsumerRequest(new PersonName("John", "Doe")))
                .contentType("application/json")
                .when()
                .post(consumerBaseUrl())
                .then()
                .statusCode(200)
                .extract()
                .path("consumerId");
        assertNotNull(consumerId);
        return consumerId;
    }

    private void verifyAccountCreatedForConsumer(int consumerId) {
        Eventually.eventually(() ->
                given()
                        .when()
                        .get(accountingBaseUrl(Integer.toString(consumerId)))
                        .then()
                        .statusCode(200));
    }

    private int createRestaurant() {
        Integer restaurantId = given()
                .body(new CreateRestaurantRequest(RESTAURANT_NAME,
                        new RestaurantMenu(Collections.singletonList(new MenuItem(CHICKEN_VINDALOO_MENU_ITEM_ID, "Chicken VIndaloo", priceOfChickendVindaloo)))))
                .contentType("application/json")
                .when()
                .post(restaurantBaseUrl())
                .then()
                .statusCode(200)
                .extract()
                .path("id");

        assertNotNull(restaurantId);
        return restaurantId;
    }

    private void verifyRestaurantCreatedInKitchenService(int restaurantId) {
        Eventually.eventually(String.format("verifyRestaurantCreatedInKitchenService %s", restaurantId), () ->
                given()
                        .when()
                        .get(kitchenRestaurantBaseUrl(Integer.toString(restaurantId)))
                        .then()
                        .statusCode(200));
    }

    private void verifyRestaurantCreatedInOrderService(int restaurantId) {
        Eventually.eventually(String.format("verifyRestaurantCreatedInOrderService %s", restaurantId), () ->
                given()
                        .when()
                        .get(orderRestaurantBaseUrl(Integer.toString(restaurantId)))
                        .then()
                        .statusCode(200));
    }

    private int createOrder(int consumerId, int restaurantId) {
        Integer orderId = given()
                .body(new CreateOrderRequest(consumerId, restaurantId, Collections.singletonList(new CreateOrderRequest.LineItem(CHICKEN_VINDALOO_MENU_ITEM_ID, 5))))
                .contentType("application/json")
                .when()
                .post(orderBaseUrl())
                .then()
                .statusCode(200)
                .extract()
                .path("orderId");

        assertNotNull(orderId);
        return orderId;
    }

    private void verifyOrderAuthorized(int orderId) {
        Eventually.eventually(String.format("verifyOrderApproved %s", orderId), () -> {
            String state = given()
                    .when()
                    .get(orderBaseUrl(Integer.toString(orderId)))
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("orderInfo.state");
            assertEquals("APPROVED", state);
        });
    }

    private void verifyOrderHistoryUpdated(int orderId, int consumerId, String expectedState) {
        Eventually.eventually(String.format("verifyOrderHistoryUpdated %s", orderId), () -> {
            String state = given()
                    .when()
                    .get(orderHistoryBaseUrl() + "?consumerId=" + consumerId)
                    .then()
                    .statusCode(200)
                    .body("orders[0].restaurantName", equalTo(RESTAURANT_NAME))
                    .extract()
                    .path("orders[0].status"); // TODO state?
            assertEquals(expectedState, state);
        });
    }

}
