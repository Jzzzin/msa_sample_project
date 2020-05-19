package com.bloknoma.ftgo.apigateway.proxies;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KitchenService {
    // 티켓 정보 조회 - 지원 안함
    public Mono<TicketInfo> findTicketById(String ticketId) {
        return Mono.error(new UnsupportedOperationException());
    }
}
