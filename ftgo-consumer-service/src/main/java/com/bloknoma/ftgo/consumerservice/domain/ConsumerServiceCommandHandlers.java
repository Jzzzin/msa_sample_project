package com.bloknoma.ftgo.consumerservice.domain;

import com.bloknoma.ftgo.consumerservice.api.ConsumerServiceChannels;
import com.bloknoma.ftgo.consumerservice.api.ValidateOrderByConsumer;
import com.bloknoma.ftgo.consumerservice.domain.exception.ConsumerVerificationFailedException;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

// 커맨드 핸들러
public class ConsumerServiceCommandHandlers {

    @Autowired
    private ConsumerService consumerService;

    // 커맨드 라우팅
    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel(ConsumerServiceChannels.consumerServiceChannel)
                .onMessage(ValidateOrderByConsumer.class, this::validateOrderByConsumer)
                .build();
    }

    // 주문 검증
    private Message validateOrderByConsumer(CommandMessage<ValidateOrderByConsumer> cm) {
        try {
            consumerService.validateOrderByConsumer(cm.getCommand().getConsumerId(), cm.getCommand().getOrderTotal());
            return withSuccess();
        } catch (ConsumerVerificationFailedException e) {
            return withFailure();
        }
    }
}
