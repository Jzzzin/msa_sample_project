package com.bloknoma.ftgo.orderservice.domain.repository;

import com.bloknoma.ftgo.orderservice.domain.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
