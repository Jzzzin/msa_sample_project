package com.bloknoma.ftgo.orderservice.sagaparticipants;

import com.bloknoma.ftgo.consumerservice.api.ConsumerServiceChannels;
import com.bloknoma.ftgo.consumerservice.api.ValidateOrderByConsumer;
import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;

// consumer service proxy
public class ConsumerServiceProxy {

    public final CommandEndpoint<ValidateOrderByConsumer> validateOrder = CommandEndpointBuilder
            .forCommand(ValidateOrderByConsumer.class)
            .withChannel(ConsumerServiceChannels.consumerServiceChannel)
            .withReply(Success.class)
            .build();
}
