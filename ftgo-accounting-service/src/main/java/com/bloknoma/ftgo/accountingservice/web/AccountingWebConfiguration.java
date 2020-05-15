package com.bloknoma.ftgo.accountingservice.web;

import com.bloknoma.ftgo.accountingservice.domain.AccountingServiceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AccountingServiceConfiguration.class)
@ComponentScan
public class AccountingWebConfiguration {
}
