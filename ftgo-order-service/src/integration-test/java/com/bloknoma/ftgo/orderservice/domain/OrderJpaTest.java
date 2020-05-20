package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import com.bloknoma.ftgo.orderservice.domain.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.CONSUMER_ID;
import static com.bloknoma.ftgo.orderservice.OrderDetailsMother.chickenVindalooLineItems;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// integration test - mysql
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderJpaTestConfiguration.class)
public class OrderJpaTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    // Order DB 저장 테스트
    @Test
    public void shouldSaveAndLoadOrder() {

        // order 저장
        long orderId = transactionTemplate.execute((ts) -> {
            Order order = new Order(CONSUMER_ID, AJANTA_ID, chickenVindalooLineItems());
            orderRepository.save(order);
            return order.getId();
        });

        // order 조회
        transactionTemplate.execute((ts) -> {
            Order order = orderRepository.findById(orderId).get();

            assertNotNull(order);
            assertEquals(OrderState.APPROVAL_PENDING, order.getState());
            assertEquals(AJANTA_ID, order.getRestaurantId().longValue());
            assertEquals(CONSUMER_ID, order.getConsumerId().longValue());
            assertEquals(chickenVindalooLineItems(), order.getLineItems());
            return null;
        });

    }

}
