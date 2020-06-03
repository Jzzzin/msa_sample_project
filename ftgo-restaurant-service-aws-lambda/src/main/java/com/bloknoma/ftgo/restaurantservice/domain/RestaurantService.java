package com.bloknoma.ftgo.restaurantservice.domain;

import com.bloknoma.ftgo.restaurantservice.events.CreateRestaurantRequest;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantCreated;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Transactional
public class RestaurantService {

    private RestaurantRepository restaurantRepository;

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    public RestaurantService() {
    }

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    // 레스토랑 추가
    public Restaurant create(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant(request.getName(), request.getMenu());
        // DB 저장
        restaurantRepository.save(restaurant);
        // 이벤트 발행
        domainEventPublisher.publish(Restaurant.class, restaurant.getId(), Collections.singletonList(new RestaurantCreated(request.getName(), request.getMenu())));
        return restaurant;
    }

    // 레스토랑 조회
    public Optional<Restaurant> findById(long restaurantId) {
        return restaurantRepository.findById(restaurantId);
    }
}
