package com.bloknoma.ftgo.consumerservice.domain;

import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.common.PersonName;
import com.bloknoma.ftgo.consumerservice.domain.exception.ConsumerNotFoundException;
import com.bloknoma.ftgo.consumerservice.domain.repository.ConsumerRepository;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public class ConsumerService {

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    // 주문 검증
    public void validateOrderByConsumer(long consumerId, Money orderTotal) {
        Optional<Consumer> consumer = consumerRepository.findById(consumerId);
        consumer.orElseThrow(ConsumerNotFoundException::new).validateOrderByConsumer(orderTotal);
    }

    // 고객 추가
    public ResultWithEvents<Consumer> create(PersonName name) {
        // 고객 추가 처리
        ResultWithEvents<Consumer> rwe = Consumer.create(name);
        // DB 저장
        consumerRepository.save(rwe.result);
        // 이벤트 발행
        domainEventPublisher.publish(Consumer.class, rwe.result.getId(), rwe.events);
        return rwe;
    }

    // 고객 조회
    public Optional<Consumer> findById(long consumerId) {
        return consumerRepository.findById(consumerId);
    }
}
