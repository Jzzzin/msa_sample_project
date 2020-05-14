package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.accountservice.api.AccountingServiceChannels;
import com.bloknoma.ftgo.accountservice.api.AuthorizeCommand;
import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;

// accounting service proxy
public class AccountingServiceProxy {

    public final CommandEndpoint<AuthorizeCommand> authorize = CommandEndpointBuilder
            .forCommand(AuthorizeCommand.class)
            .withChannel(AccountingServiceChannels.accountingServiceChannel)
            .withReply(Success.class)
            .build();

}
