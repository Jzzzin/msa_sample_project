package com.bloknoma.ftgo.kitchenservice.web;

import com.bloknoma.ftgo.kitchenservice.domain.KitchenDomainConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KitchenDomainConfiguration.class)
@ComponentScan
public class KitchenServiceWebConfiguration {
}
