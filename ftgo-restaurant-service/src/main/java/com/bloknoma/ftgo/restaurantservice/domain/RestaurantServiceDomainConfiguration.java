package com.bloknoma.ftgo.restaurantservice.domain;

import com.bloknoma.ftgo.common.CommonConfiguration;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
@EntityScan
@Import({TramEventsPublisherConfiguration.class, CommonConfiguration.class})
public class RestaurantServiceDomainConfiguration {

    @Bean
    public RestaurantService restaurantService() {
        return new RestaurantService();
    }
}
