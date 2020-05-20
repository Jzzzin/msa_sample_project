package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.orderservice.domain.repository.RestaurantRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.support.TransactionTemplate;

import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_ID;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT_MENU_ITEMS;
import static com.bloknoma.ftgo.orderservice.RestaurantMother.AJANTA_RESTAURANT_NAME;

// integration test - mysql
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderJpaTestConfiguration.class)
public class RestaurantJpaTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    // 레스토랑 DB 저장 테스트
    @Test
    public void shouldSaveRestaurant() {

        // 레스토랑 저장
        transactionTemplate.execute((ts) -> {
            Restaurant restaurant = new Restaurant(AJANTA_ID, AJANTA_RESTAURANT_NAME, AJANTA_RESTAURANT_MENU_ITEMS);
            restaurantRepository.save(restaurant);
            return null;
        });

        // 중복 저장
        transactionTemplate.execute((ts) -> {
            Restaurant restaurant = new Restaurant(AJANTA_ID, AJANTA_RESTAURANT_NAME, AJANTA_RESTAURANT_MENU_ITEMS);
            restaurantRepository.save(restaurant);
            return null;
        });
    }

}
