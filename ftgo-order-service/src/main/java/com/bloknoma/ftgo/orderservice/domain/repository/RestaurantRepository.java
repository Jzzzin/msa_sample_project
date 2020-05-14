package com.bloknoma.ftgo.orderservice.domain.repository;

import com.bloknoma.ftgo.orderservice.domain.Restaurant;
import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
}
