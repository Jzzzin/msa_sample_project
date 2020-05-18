package com.bloknoma.ftgo.consumerservice.web;

import com.bloknoma.ftgo.consumerservice.api.web.CreateConsumerRequest;
import com.bloknoma.ftgo.consumerservice.api.web.CreateConsumerResponse;
import com.bloknoma.ftgo.consumerservice.domain.Consumer;
import com.bloknoma.ftgo.consumerservice.domain.ConsumerService;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// REST API
@RestController
@RequestMapping(path = "/consumers")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    // 고객 추가
    @RequestMapping(method = RequestMethod.POST)
    public CreateConsumerResponse create(@RequestBody CreateConsumerRequest request) {
        ResultWithEvents<Consumer> result = consumerService.create(request.getName());
        return new CreateConsumerResponse(result.result.getId());
    }

    // 고객 조회
    @RequestMapping(path = "/{consumerId}", method = RequestMethod.GET)
    public ResponseEntity<GetConsumerResponse> get(@PathVariable long consumerId) {
        return consumerService.findById(consumerId)
                .map(consumer -> new ResponseEntity<>(new GetConsumerResponse(consumer.getName()), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
