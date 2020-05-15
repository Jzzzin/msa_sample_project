package com.bloknoma.ftgo.accountingservice.messaging;

import com.bloknoma.ftgo.accountingservice.domain.*;
import com.bloknoma.ftgo.accountingservice.domain.exception.AccountDisabledException;
import com.bloknoma.ftgo.accountservice.api.AccountDisabledReply;
import com.bloknoma.ftgo.accountservice.api.AuthorizeCommand;
import com.bloknoma.ftgo.accountservice.api.ReverseAuthorizationCommand;
import com.bloknoma.ftgo.accountservice.api.ReviseAuthorization;
import io.eventuate.sync.AggregateRepository;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.sagas.eventsourcingsupport.UpdatingOptionsBuilder.replyingTo;

// 커맨드 핸들러
public class AccountingServiceCommandHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AggregateRepository<Account, AccountCommand> accountrepository;

    // 커맨드 라우팅
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("accountingService")
                .onMessage(AuthorizeCommand.class, this::authorize)
                .onMessage(ReverseAuthorizationCommand.class, this::reverseAuthorization)
                .onMessage(ReviseAuthorization.class, this::reviseAuthorization)
                .build();
    }

    // 승인
    public void authorize(CommandMessage<AuthorizeCommand> cm) {
        AuthorizeCommand command = cm.getCommand();

        // 승인 처리
        accountrepository.update(Long.toString(command.getConsumerId()),
                makeAuthorizeCommandInternal(command),
                replyingTo(cm)
                        .catching(AccountDisabledException.class, () -> withFailure(new AccountDisabledReply()))
                        .build());
    }

    // reverse 승인
    public void reverseAuthorization(CommandMessage<ReverseAuthorizationCommand> cm) {
        ReverseAuthorizationCommand command = cm.getCommand();

        // 승인 처리
        accountrepository.update(Long.toString(command.getConsumerId()),
                makeReverseAuthorizeCommandInternal(command),
                replyingTo(cm)
                        .catching(AccountDisabledException.class, () -> withFailure(new AccountDisabledReply()))
                        .build());
    }

    // 수정 승인
    public void reviseAuthorization(CommandMessage<ReviseAuthorization> cm) {
        ReviseAuthorization command = cm.getCommand();

        // 승인 처리
        accountrepository.update(Long.toString(command.getConsumerId()),
                makeReviseAuthorizeCommandInternal(command),
                replyingTo(cm)
                        .catching(AccountDisabledException.class, () -> withFailure(new AccountDisabledReply()))
                        .build());
    }

    private AuthorizeCommandInternal makeAuthorizeCommandInternal(AuthorizeCommand command) {
        return new AuthorizeCommandInternal(Long.toString(command.getConsumerId()), Long.toString(command.getOrderId()), command.getOrderTotal());
    }

    private ReverseAuthorizationCommandInternal makeReverseAuthorizeCommandInternal(ReverseAuthorizationCommand command) {
        return new ReverseAuthorizationCommandInternal(Long.toString(command.getConsumerId()), Long.toString(command.getOrderId()), command.getOrderTotal());
    }

    private ReviseAuthorizationCommandInternal makeReviseAuthorizeCommandInternal(ReviseAuthorization command) {
        return new ReviseAuthorizationCommandInternal(Long.toString(command.getConsumerId()), Long.toString(command.getOrderid()), command.getOrderTotal());
    }
}
