package com.bloknoma.ftgo.consumerservice.domain;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.common.PersonName;
import com.bloknoma.ftgo.consumerservice.api.ConsumerCreated;
import io.eventuate.tram.events.publisher.ResultWithEvents;

import javax.persistence.*;

@Entity
@Table(name = "consumers")
@Access(AccessType.FIELD)
public class Consumer {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private PersonName name;

    private Consumer() {
    }

    public Consumer(PersonName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public PersonName getName() {
        return name;
    }

    // 주문 검증
    public void validateOrderByConsumer(Money orderTotal) {
        // TODO implement some business logic
    }

    // 고객 추가
    public static ResultWithEvents<Consumer> create(PersonName name) {
        return new ResultWithEvents<>(new Consumer(name), new ConsumerCreated());
    }
}
