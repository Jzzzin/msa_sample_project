package com.bloknoma.ftgo.cqrs.orderhistory.web;

import com.bloknoma.ftgo.cqrs.orderhistory.dynamodb.OrderHistoryDynamoDBConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(OrderHistoryDynamoDBConfiguration.class)
public class OrderHistoryWebConfiguration {
}
