package com.bloknoma.ftgo.restaurantservice.lambda;

import com.bloknoma.ftgo.restaurantservice.domain.RestaurantService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

// integration test - mysql
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestaurantServiceLambdaConfiguration.class)
public class RestaurantServiceLambdaConfigurationTest {

    @Autowired
    private RestaurantService restaurantService;

    @Test
    public void shouldInitialize() {
    }
}
