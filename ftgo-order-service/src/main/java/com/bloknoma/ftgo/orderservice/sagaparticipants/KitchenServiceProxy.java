package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.kitchenservice.api.*;
import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;

// kitchen service proxy
public class KitchenServiceProxy {

    public final CommandEndpoint<CreateTicket> create = CommandEndpointBuilder
            .forCommand(CreateTicket.class)
            .withChannel(KitchenServiceChannels.kitchenServiceChannel)
            .withReply(CreateTicketReply.class)
            .build();

    public final CommandEndpoint<ConfirmCreateTicket> confirmCreate = CommandEndpointBuilder
            .forCommand(ConfirmCreateTicket.class)
            .withChannel(KitchenServiceChannels.kitchenServiceChannel)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<CancelCreateTicket> cancel = CommandEndpointBuilder
            .forCommand(CancelCreateTicket.class)
            .withChannel(KitchenServiceChannels.kitchenServiceChannel)
            .withReply(Success.class)
            .build();

}
