package com.bloknoma.ftgo.apigateway.proxies;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeliveryService {
    // 배달 정보 조회 - 지원 안함
    public Mono<DeliveryInfo> findDeliveryByOrderId(String orderId) {
        return Mono.error(new UnsupportedOperationException());
    }
}
