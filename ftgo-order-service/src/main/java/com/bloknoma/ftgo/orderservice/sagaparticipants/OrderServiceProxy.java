package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.orderservice.api.OrderServiceChannels;
import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;

// order service proxy
public class OrderServiceProxy {

    public final CommandEndpoint<RejectOrderCommand> reject = CommandEndpointBuilder
            .forCommand(RejectOrderCommand.class)
            .withChannel(OrderServiceChannels.orderServiceChannel)
            .withReply(Success.class)
            .build();

    public final CommandEndpoint<ApproveOrderCommand> approve = CommandEndpointBuilder
            .forCommand(ApproveOrderCommand.class)
            .withChannel(OrderServiceChannels.orderServiceChannel)
            .withReply(Success.class)
            .build();
}
