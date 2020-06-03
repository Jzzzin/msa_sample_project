package com.bloknoma.ftgo.restaurantservice.lambda;

import com.bloknoma.ftgo.restaurantservice.domain.RestaurantServiceDomainConfiguration;
import io.eventuate.tram.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RestaurantServiceDomainConfiguration.class, TramMessageProducerJdbcConfiguration.class})
@EnableAutoConfiguration
public class RestaurantServiceLambdaConfiguration {
}
