package com.bloknoma.ftgo.consumerservice.web;

import com.bloknoma.ftgo.consumerservice.domain.ConsumerServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(ConsumerServiceConfiguration.class)
public class ConsumerWebConfiguration {
}
