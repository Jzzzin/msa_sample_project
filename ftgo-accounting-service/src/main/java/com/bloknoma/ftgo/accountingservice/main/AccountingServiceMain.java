package com.bloknoma.ftgo.accountingservice.main;

import com.bloknoma.ftgo.accountingservice.messaging.AccountingMessagingConfiguration;
import com.bloknoma.ftgo.accountingservice.web.AccountingWebConfiguration;
import io.eventuate.javaclient.driver.EventuateDriverConfiguration;
import io.eventuate.tram.commands.producer.TramCommandProducerConfiguration;
import io.eventuate.tram.jdbckafka.TramJdbcKafkaConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import({AccountingMessagingConfiguration.class, AccountingWebConfiguration.class,
        TramCommandProducerConfiguration.class,
        EventuateDriverConfiguration.class,
        TramJdbcKafkaConfiguration.class})
public class AccountingServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(AccountingServiceMain.class, args);
    }
}
