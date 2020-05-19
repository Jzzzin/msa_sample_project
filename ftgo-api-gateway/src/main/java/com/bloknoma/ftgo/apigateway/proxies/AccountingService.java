package com.bloknoma.ftgo.apigateway.proxies;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccountingService {
    // 청구 정보 조회 - 지원안함
    public Mono<BillInfo> findBillByOrderId(String orderId) {
        return Mono.error(new UnsupportedOperationException());
    }
}
