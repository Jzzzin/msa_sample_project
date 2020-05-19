package com.bloknoma.ftgo.cqrs.orderhistory.dynamodb;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistory;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryDao;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryFilter;
import com.bloknoma.ftgo.orderservice.api.events.OrderLineItem;
import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import io.eventuate.common.json.mapper.JSonMapper;
import io.eventuate.tram.inmemory.TramInMemoryConfiguration;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {OrderHistoryDaoDynamoDbTest.OrderHistoryDaoDynamoDbTestConfiguration.class})
public class OrderHistoryDaoDynamoDbTest {

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan
    @Import({OrderHistoryDynamoDBConfiguration.class, TramInMemoryConfiguration.class})
    static public class OrderHistoryDaoDynamoDbTestConfiguration {

    }

    private String consumerId;
    private String orderId;
    private long restaurantId;
    private String restaurantName;

    private String chickenVindaloo;
    private Order order1;
    private Optional<SourceEvent> eventSource;

    @Autowired
    private OrderHistoryDao dao;

    @Before
    public void setup() {
        consumerId = "consumerId" + System.currentTimeMillis();
        orderId = "orderId" + System.currentTimeMillis();
        restaurantId = 101L;
        restaurantName = "Ajanta" + System.currentTimeMillis();
        chickenVindaloo = "Chicken Vindaloo" + System.currentTimeMillis();

        order1 = new Order(orderId, consumerId, OrderState.APPROVAL_PENDING, singletonList(new OrderLineItem("-1", chickenVindaloo, Money.ZERO, 0)), null, restaurantId, restaurantName);
        order1.setCreationDate(DateTime.now().minusDays(5));
        eventSource = Optional.of(new SourceEvent("Order", orderId, "11212-34343"));

        dao.addOrder(order1, eventSource);
    }

    // 주문 조회
    @Test
    public void shouldFindOrder() {
        Optional<Order> order = dao.findOrder(orderId);
        assertOrderEquals(order1, order.get());
    }

    private void assertOrderEquals(Order expected, Order order) {
        System.out.println("Expected=" + JSonMapper.toJson(expected.getLineItems()));
        System.out.println("actual  =" + JSonMapper.toJson(order.getLineItems()));
        assertEquals(expected.getLineItems(), order.getLineItems());
        assertEquals(expected.getStatus(), order.getStatus());
        assertEquals(expected.getCreationDate(), order.getCreationDate());
        assertEquals(expected.getRestaurantId(), order.getRestaurantId());
        assertEquals(expected.getRestaurantName(), order.getRestaurantName());
    }

    // 중복 업데이트 방지
    @Test
    public void shouldIgnoreDuplicateAdd() {
        dao.cancelOrder(orderId, Optional.empty());
        assertFalse(dao.addOrder(order1, eventSource));
        Optional<Order> order = dao.findOrder(orderId);
        assertEquals(OrderState.CANCELLED, order.get().getStatus());
    }

    // 주문 내역 조회
    @Test
    public void shouldFindOrders() {
        OrderHistory result = dao.findOrderHistory(consumerId, new OrderHistoryFilter());
        assertNotNull(result);
        List<Order> orders = result.getOrders();
        Order retrievedOrder = assertContainsOrderId(orderId, orders);
        assertOrderEquals(order1, retrievedOrder);
    }

    private Order assertContainsOrderId(String orderId, List<Order> orders) {
        Optional<Order> order = orders.stream().filter(o -> o.getOrderId().equals(orderId)).findFirst();
        assertTrue("Order not found", order.isPresent());
        return order.get();
    }

    // 상태 필터 주문 내역 조회
    @Test
    public void shouldFindOrdersWithStatus() throws InterruptedException {
        OrderHistory result = dao.findOrderHistory(consumerId, new OrderHistoryFilter().withStatus(OrderState.APPROVAL_PENDING));
        assertNotNull(result);
        List<Order> orders = result.getOrders();
        assertContainsOrderId(orderId, orders);
    }

    // 주문 취소
    @Test
    public void shouldCancel() throws InterruptedException {
        dao.cancelOrder(orderId, Optional.of(new SourceEvent("a", "b", "c")));
        Order order = dao.findOrder(orderId).get();
        assertEquals(OrderState.CANCELLED, order.getStatus());
    }

    // 주문 중복 취소
    @Test
    public void shouldHandleCancel() throws InterruptedException {
        assertTrue(dao.cancelOrder(orderId, Optional.of(new SourceEvent("a", "b", "c"))));
        assertFalse(dao.cancelOrder(orderId, Optional.of(new SourceEvent("a", "b", "c"))));
    }

    // 취소 주문 조회
    @Test
    public void shouldFindOrdersWithCancelledStatus() {
        OrderHistory result = dao.findOrderHistory(consumerId, new OrderHistoryFilter().withStatus(OrderState.CANCELLED));
        assertNotNull(result);
        List<Order> orders = result.getOrders();
        assertNotContainsOrderId(orderId, orders);
    }

    private void assertNotContainsOrderId(String orderId, List<Order> orders) {
        Optional<Order> order = orders.stream().filter(o -> o.getOrderId().equals(orderId)).findFirst();
        assertFalse(order.isPresent());
    }

    // 메뉴로 주문 조회
    @Test
    public void shouldFindOrdersByMenuItem() {
        OrderHistory result = dao.findOrderHistory(consumerId, new OrderHistoryFilter().withKeywords(singleton(chickenVindaloo)));
        assertNotNull(result);
        List<Order> orders = result.getOrders();
        assertContainsOrderId(orderId, orders);
    }

    // 정렬해서 주문 조회
    @Test
    public void shouldReturnOrdersSorted() {
        // 주문 추가
        String orderId2 = "orderId" + System.currentTimeMillis();
        Order order2 = new Order(orderId2, consumerId, OrderState.APPROVAL_PENDING, singletonList(new OrderLineItem("-1", "Lamb 65", Money.ZERO, -1)), null, restaurantId, restaurantName);
        order2.setCreationDate(DateTime.now().minusDays(1));
        dao.addOrder(order2, eventSource);

        OrderHistory result = dao.findOrderHistory(consumerId, new OrderHistoryFilter());
        List<Order> orders = result.getOrders();

        int idx1 = indexOf(orders, orderId);
        int idx2 = indexOf(orders, orderId2);
        assertTrue(idx2 < idx1);
    }

    private int indexOf(List<Order> orders, String orderId) {
        Order order = orders.stream().filter(o -> o.getOrderId().equals(orderId)).findFirst().get();
        return orders.indexOf(order);
    }

    // 페이지네이션
    @Test
    public void shouldPaginateResults() {
        // 주문 추가
        String orderid2 = "orderId" + System.currentTimeMillis();
        Order order2 = new Order(orderid2, consumerId, OrderState.APPROVAL_PENDING, singletonList(new OrderLineItem("-1", "Lamb 65", Money.ZERO, -1)), null, restaurantId, restaurantName);
        order2.setCreationDate(DateTime.now().minusDays(1));
        dao.addOrder(order2, eventSource);

        OrderHistory result = dao.findOrderHistory(consumerId, new OrderHistoryFilter().withPageSize(1));

        assertEquals(1, result.getOrders().size());
        assertTrue(result.getStartKey().isPresent());

        OrderHistory result2 = dao.findOrderHistory(consumerId, new OrderHistoryFilter().withPageSize(1).withStartKeyToken(result.getStartKey()));

        assertEquals(1, result2.getOrders().size());
    }
}
