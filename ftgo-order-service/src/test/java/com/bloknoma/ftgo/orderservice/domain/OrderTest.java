package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.orderservice.RestaurantMother;
import com.bloknoma.ftgo.orderservice.api.events.OrderCreatedEvent;
import com.bloknoma.ftgo.orderservice.api.events.OrderDomainEvent;
import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import com.bloknoma.ftgo.orderservice.api.events.OrderAuthorized;
import com.bloknoma.ftgo.orderservice.domain.value.OrderRevision;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.*;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.CHICKEN_VINDALOO_PRICE;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

// unit test
public class OrderTest {

    private ResultWithDomainEvents<Order, OrderDomainEvent> createResult;
    private Order order;

    // 주문 생성
    @Before
    public void setUp() throws Exception {
        createResult = Order.createOrder(CONSUMER_ID, AJANTA_RESTAURANT, chickenVindalooLineItems());
        order = createResult.result;
    }

    // 주문 생성 테스트
    @Test
    public void shouldCreateOrder() {
        // 이벤트 확인
        assertEquals(singletonList(new OrderCreatedEvent(CHICKEN_VINDALOO_ORDER_DETAILS, RestaurantMother.AJANTA_RESTAURANT_NAME)), createResult.events);
        // 상태 확인
        assertEquals(OrderState.APPROVAL_PENDING, order.getState());
        // ...
    }

    // 주문 총액 테스트
    @Test
    public void shouldCalculateTotal() {
        assertEquals(CHICKEN_VINDALOO_PRICE.multiply(CHICKEN_VINDALOO_QUANTITY), order.getOrderTotal());
    }

    // 주문 승인 테스트
    @Test
    public void shouldAuthorize() {
        // 주문 승인 처리
        List<OrderDomainEvent> events = order.noteApproved();
        // 이벤트 확인
        assertEquals(singletonList(new OrderAuthorized()), events);
        // 상태 확인
        assertEquals(OrderState.APPROVED, order.getState());
    }

    // 주문 변경 테스트
    @Test
    public void shouldReviseOrder() {
        // 주문 승인 처리
        order.noteApproved();

        // 주문 변경 정보
        OrderRevision orderRevision = new OrderRevision(Optional.empty(), Collections.singletonMap("1", 10));

        // 주문 변경 처리
        ResultWithDomainEvents<LineItemQuantityChange, OrderDomainEvent> result = order.revise(orderRevision);

        // 변경 진행 중 주문 총액 확인
        assertEquals(CHICKEN_VINDALOO_PRICE.multiply(10), result.result.getNewOrderTotal());

        // 주문 변경 확인 처리
        order.confirmRevision(orderRevision);

        // 변경 확인 후 주문 총액 확인
        assertEquals(CHICKEN_VINDALOO_PRICE.multiply(10), order.getOrderTotal());
    }
}

