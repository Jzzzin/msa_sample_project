package com.bloknoma.ftgo.cqrs.orderhistory;

import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import org.joda.time.DateTime;

import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;

// 주문 이력 검색 조건
public class OrderHistoryFilter {
    // 날짜
    private DateTime since = DateTime.now().minusDays(30);
    // 주문 상태
    private Optional<OrderState> status = Optional.empty();
    // 키워드
    private Set<String> keywords = emptySet();
    // 페이징 관련
    private Optional<String> startKeyToken = Optional.empty();
    private Optional<Integer> pageSize = Optional.empty();

    public DateTime getSince() {
        return since;
    }

    public Optional<OrderState> getStatus() {
        return status;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public Optional<String> getStartKeyToken() {
        return startKeyToken;
    }

    public Optional<Integer> getPageSize() {
        return pageSize;
    }

    public OrderHistoryFilter withStatus(OrderState status) {
        this.status = Optional.of(status);
        return this;
    }

    public OrderHistoryFilter withKeywords(Set<String> keywords) {
        this.keywords = keywords;
        return this;
    }

    public OrderHistoryFilter withStartKeyToken(Optional<String> startKeyToken) {
        this.startKeyToken = startKeyToken;
        return this;
    }

    public OrderHistoryFilter withPageSize(int pageSize) {
        this.pageSize = Optional.of(pageSize);
        return this;
    }
}
