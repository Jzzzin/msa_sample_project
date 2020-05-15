package com.bloknoma.ftgo.accountingservice.domain;

import io.eventuate.sync.AggregateRepository;
import io.eventuate.EntityWithIdAndVersion;
import io.eventuate.SaveOptions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class AccountingService {
    @Autowired
    private AggregateRepository<Account, AccountCommand> accountRepository;

    // 계좌 생성
    public void create(String aggregateId) {
        EntityWithIdAndVersion<Account> account = accountRepository.save(new CreateAccountCommand(),
                Optional.of(new SaveOptions().withId(aggregateId)));
    }
}
