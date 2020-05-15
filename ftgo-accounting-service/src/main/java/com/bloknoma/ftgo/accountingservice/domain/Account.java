package com.bloknoma.ftgo.accountingservice.domain;

import com.bloknoma.ftgo.accountingservice.domain.event.AccountAuthorizedEvent;
import com.bloknoma.ftgo.accountingservice.domain.event.AccountCreatedEvent;
import io.eventuate.Event;
import io.eventuate.ReflectiveMutableCommandProcessingAggregate;
import io.eventuate.tram.sagas.eventsourcingsupport.SagaReplyRequestedEvent;

import java.util.Collections;
import java.util.List;

import static io.eventuate.EventUtil.events;

public class Account extends ReflectiveMutableCommandProcessingAggregate<Account, AccountCommand> {

    // 계좌 생성 처리
    public List<Event> process(CreateAccountCommand command) {
        return events(new AccountCreatedEvent());
    }

    // 계좌 생성 적용
    public void apply(AccountCreatedEvent event) {

    }

    // 계좌 승인 처리
    public List<Event> process(AuthorizeCommandInternal command) {
        return events(new AccountAuthorizedEvent());
    }

    // 계좌 승인 적용
    public void apply(AccountAuthorizedEvent event) {

    }

    // reverse 승인 처리
    public List<Event> process(ReverseAuthorizationCommandInternal command) {
        return Collections.emptyList();
    }
    // 수정 승인 처리
    public List<Event> process(ReviseAuthorizationCommandInternal command) {
        return Collections.emptyList();
    }

    public void apply(SagaReplyRequestedEvent event) {
        // TODO - need a way to not need this method
    }

}
