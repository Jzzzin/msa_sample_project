package com.bloknoma.ftgo.accountingservice.messaging;

import com.bloknoma.ftgo.accountingservice.domain.AccountingService;
import com.bloknoma.ftgo.consumerservice.api.ConsumerCreated;
import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

// 이벤트 컨슈머
public class AccountingEventConsumer {

    @Autowired
    private AccountingService accountingService;

    // 이벤트 라우팅
    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("com.bloknoma.ftgo.consumerservice.domain.Consumer")
                .onEvent(ConsumerCreated.class, this::createAccount)  // TODO this is hack to get the correct package
                .build();
    }

    // 계좌 생성
    private void createAccount(DomainEventEnvelope<ConsumerCreated> dee) {
        accountingService.create(dee.getAggregateId());
    }

}
