package com.bloknoma.ftgo.apigateway;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiGatewayIntegrationTestConfiguration.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"order.destinations.orderServiceUrl=http://localhost:8082",
                "order.destinations.orderHistoryServiceUrl=http:/localhost:8083",
                "consumer.destinations.consumerServiceUrl=http://localhost:9999"})
public class ApiGatewayIntegrationTest {

    @LocalServerPort
    private int port;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8082); // No-args constructor defaults to port 8080

    // 주문 생성
    @Test
    public void shouldProxyCreateOrder() {

        stubFor(post(urlEqualTo("/orders"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        WebClient client = WebClient.create("http://localhost:" + port + "/orders");

        ResponseEntity<String> z = client
                .post()
                .body(BodyInserters.fromObject("{}"))
                .exchange()
                .flatMap(r -> r.toEntity(String.class))
                .block();

        assertNotNull(z);
        assertEquals(HttpStatus.OK, z.getStatusCode());
        assertEquals("<response>Some content</response>", z.getBody());

        verify(postRequestedFor(urlMatching("/orders")));
    }


}
