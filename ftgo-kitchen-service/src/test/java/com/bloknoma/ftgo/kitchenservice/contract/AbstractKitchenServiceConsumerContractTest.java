package com.bloknoma.ftgo.kitchenservice.contract;

import com.bloknoma.ftgo.kitchenservice.api.TicketDetails;
import com.bloknoma.ftgo.kitchenservice.domain.KitchenService;
import com.bloknoma.ftgo.kitchenservice.domain.Ticket;
import com.bloknoma.ftgo.kitchenservice.messagehandlers.KitchenServiceMessageHandlersConfiguration;
import io.eventuate.tram.springcloudcontractsupport.EventuateContractVerifierConfiguration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

// unit test - contract
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbstractKitchenServiceConsumerContractTest.TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
public abstract class AbstractKitchenServiceConsumerContractTest {

    @Configuration
    @Import({KitchenServiceMessageHandlersConfiguration.class, EventuateContractVerifierConfiguration.class})
    public static class TestConfiguration {

        @Bean
        public KitchenService kitchenService() {
            // 목 설정
            return mock(KitchenService.class);
        }
    }

    @Autowired
    private KitchenService kitchenService;

    @Before
    public void setup() {
        reset(kitchenService);
        // 목 설정
        when(kitchenService.createTicket(eq(1L), eq(99L), any(TicketDetails.class)))
                .thenReturn(new Ticket(1L, 99L, new TicketDetails(Collections.emptyList())));
    }
}
