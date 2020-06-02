package com.bloknoma.ftgo.cqrs.orderhistory.dynamodb;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.bloknoma.ftgo.common.Money;
import com.bloknoma.ftgo.cqrs.orderhistory.Location;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistory;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryDao;
import com.bloknoma.ftgo.cqrs.orderhistory.OrderHistoryFilter;
import com.bloknoma.ftgo.orderservice.api.events.OrderLineItem;
import com.bloknoma.ftgo.orderservice.api.events.OrderState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.expr.Expr;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.Op;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.BreakIterator;
import java.util.*;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class OrderHistoryDaoDynamoDb implements OrderHistoryDao {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // 테이블 명
    public static final String FTGO_ORDER_HISTORY_BY_ID = "ftgo-order-history";
    // 보조인덱스 명
    public static final String FTGO_ORDER_HISTORY_BY_CONSUMER_ID_AND_DATE = "ftgo-order-history-by-consumer-id-and-creation-time";
    public static final String ORDER_STATUS_FIELD = "orderStatus";
    private static final String DELIVERY_STATUS_FIELD = "deliveryStatus";

    private final DynamoDB dynamoDB;

    private Table table;
    private Index index;

    public OrderHistoryDaoDynamoDb(DynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
        table = this.dynamoDB.getTable(FTGO_ORDER_HISTORY_BY_ID);
        index = table.getIndex(FTGO_ORDER_HISTORY_BY_CONSUMER_ID_AND_DATE);
    }

    // pk 생성
    static PrimaryKey makePrimaryKey(String orderId) {
        return new PrimaryKey().addComponent("orderId", orderId);
    }

    // 주문 추가
    @Override
    public boolean addOrder(Order order, Optional<SourceEvent> eventSource) {
        UpdateItemSpec spec = new UpdateItemSpec()
                .withPrimaryKey("orderId", order.getOrderId()) // 기본 키
                .withUpdateExpression("SET orderStatus = :orderStatus, " +
                        "creationDate = :creationDate, consumerId = :consumerId, lineItems =" +
                        " :lineItems, keywords = :keywords, restaurantId = :restaurantId, " +
                        " restaurantName = :restaurantName") // 업데이트 표현식
                .withValueMap(new Maps()
                        .add(":orderStatus", order.getStatus().toString())
                        .add(":consumerId", order.getConsumerId())
                        .add(":creationDate", order.getCreationDate().getMillis())
                        .add(":lineItems", mapLineItems(order.getLineItems()))
                        .add(":keywords", mapKeywords(order))
                        .add(":restaurantId", order.getRestaurantId())
                        .add(":restaurantName", order.getRestaurantName())
                        .map()) // 업데이트 표현식 값
                .withReturnValues(ReturnValue.NONE);
        return idempotentUpdate(spec, eventSource);
    }

    // 중복 업데이트 방지 조건부 표현식 추가
    private boolean idempotentUpdate(UpdateItemSpec spec, Optional<SourceEvent> eventSource) {
        try {
            table.updateItem(eventSource.map(es -> es.addDuplicateDetection(spec))
                    .orElse(spec));
            return true;
        } catch (ConditionalCheckFailedException e) {
            logger.error("not updated {}", eventSource);
            // Do nothing
            return false;
        }
    }

    // 키워드 추가 - 레스토랑 이름, 메뉴 이름
    private Set mapKeywords(Order order) {
        Set<String> keywords = new HashSet<>();
        keywords.addAll(tokenize(order.getRestaurantName()));
        keywords.addAll(tokenize(order.getLineItems().stream().map(OrderLineItem::getName).collect(toList())));
        return keywords;
    }

    private Set<String> tokenize(Collection<String> text) {
        return text.stream().flatMap(t -> tokenize(t).stream()).collect(toSet());
    }

    private Set<String> tokenize(String text) {
        Set<String> result = new HashSet<>();
        BreakIterator bi = BreakIterator.getWordInstance();
        bi.setText(text);
        int lastIndex = bi.first();
        while (lastIndex != BreakIterator.DONE) {
            int firstIndex = lastIndex;
            lastIndex = bi.next();
            if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
                String word = text.substring(firstIndex, lastIndex);
                result.add(word);
            }
        }
        return result;
    }

    // 메뉴 추가
    private List mapLineItems(List<OrderLineItem> lineItems) {
        return lineItems.stream().map(this::mapOrderLineItem).collect(toList());
    }

    private Map mapOrderLineItem(OrderLineItem orderLineItem) {
        return new Maps()
                .add("menuItemName", orderLineItem.getName())
                .add("menuItemId", orderLineItem.getMenuItemId())
                .add("price", orderLineItem.getPrice().asString())
                .add("quantity", orderLineItem.getQuantity())
                .map();
    }

    // 주문 내역 조회
    @Override
    public OrderHistory findOrderHistory(String consumerId, OrderHistoryFilter filter) {
        QuerySpec spec = new QuerySpec()
                .withScanIndexForward(false) // 최근 순서대로 반환
                .withHashKey("consumerId", consumerId)
                .withRangeKeyCondition(new RangeKeyCondition("creationDate").gt(filter.getSince().getMillis())); // 최댓값

        filter.getStartKeyToken().ifPresent(token -> spec.withExclusiveStartKey(toStartingPrimaryKey(token))); // 페이지네이션

        Map<String, Object> valuesMap = new HashMap<>();

        // 필터 표현식
        String filterExpression = Expressions.and(
                keywordFilterExpression(valuesMap, filter.getKeywords()),
                statusFilterExpression(valuesMap, filter.getStatus())
        );

        if (!valuesMap.isEmpty()) {
            spec.withValueMap(valuesMap);
        }

        if (StringUtils.isNotBlank(filterExpression)) {
            spec.withFilterExpression(filterExpression);
        }

        System.out.print("filterExpression.toString()=" + filterExpression);

        filter.getPageSize().ifPresent(spec::withMaxResultSize); // 페이지 크기

        // 쿼리 호출
        ItemCollection<QueryOutcome> result = index.query(spec);

        return new OrderHistory(StreamSupport.stream(result.spliterator(), false)
                .map(this::toOrder).collect(toList()), // Order 생성
                Optional.ofNullable(result.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey()).map(this::toStartKeyToken)); // 페이지네이션
    }

    // 시작 값 설정
    private PrimaryKey toStartingPrimaryKey(String token) {
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> map;
        try {
            map = om.readValue(token, Map.class);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        PrimaryKey pk = new PrimaryKey();
        map.entrySet().forEach(key -> {
            pk.addComponent(key.getKey(), key.getValue());
        });
        return pk;
    }

    // 시작 값 토큰 설정
    private String toStartKeyToken(Map<String, AttributeValue> lastEvaluatedKey) {
        Map<String, Object> map = new HashMap<>();
        lastEvaluatedKey.entrySet().forEach(entry -> {
            String value = entry.getValue().getS();
            if (value == null) {
                value = entry.getValue().getN();
                map.put(entry.getKey(), Long.parseLong(value));
            } else {
                map.put(entry.getKey(), value);
            }
        });
        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    // 키워드 필터
    private String keywordFilterExpression(Map<String, Object> expressionAttributeValuesBuilder, Set<String> kw) {
        Set<String> keywords = tokenize(kw);
        if (keywords.isEmpty()) {
            return "";
        }
        String cuisinesExpression = "";
        int idx = 0;
        for (String cuisine : keywords) {
            String var = ":keyword" + idx;
            String cuisineExpression = String.format("contains(keywords, %s)", var);
            cuisinesExpression = Expressions.or(cuisinesExpression, cuisineExpression);
            expressionAttributeValuesBuilder.put(var, cuisine);
        }
        return cuisinesExpression;
    }

    // 상태 필터
    private Optional<String> statusFilterExpression(Map<String, Object> expressionAttributeValuesBuilder, Optional<OrderState> status) {
        return status.map(s -> {
            expressionAttributeValuesBuilder.put(":orderStatus", s.toString());
            return "orderStatus = :orderStatus";
        });
    }

    // 주문 상태 변경
    @Override
    public boolean updateOrderState(String orderId, OrderState newState, Optional<SourceEvent> eventSource) {
        UpdateItemSpec spec = new UpdateItemSpec()
                .withPrimaryKey("orderId", orderId)
                .withUpdateExpression("SET #orderStatus = :orderStatus")
                .withNameMap(Collections.singletonMap("#orderStatus", ORDER_STATUS_FIELD))
                .withValueMap(Collections.singletonMap(":orderStatus", newState.toString()))
                .withReturnValues(ReturnValue.NONE);
        return idempotentUpdate(spec, eventSource);
    }

    // 티켓 준비 시작 - 지원 안함
    @Override
    public void noteTicketPreparationStarted(String orderId) {
        throw new UnsupportedOperationException();
    }

    // 티켓 준비 완료 - 지원 안함
    @Override
    public void noteTicketPreparationCompleted(String orderId) {
        throw new UnsupportedOperationException();
    }

    // 배달 픽업
    @Override
    public void notePickedUp(String orderId, Optional<SourceEvent> eventSource) {
        UpdateItemSpec spec = new UpdateItemSpec()
                .withPrimaryKey("orderId", orderId)
                .withUpdateExpression("SET #deliveryStatus = :deliveryStatus")
                .withNameMap(Collections.singletonMap("#deliveryStatus", DELIVERY_STATUS_FIELD))
                .withValueMap(Collections.singletonMap(":deliveryStatus", DeliveryStatus.PICKED_UP.toString()))
                .withReturnValues(ReturnValue.NONE);
        idempotentUpdate(spec, eventSource);
    }

    // 위치 업데이트 - 지원 안함
    @Override
    public void updateLocation(String orderId, Location location) {
        throw new UnsupportedOperationException();
    }

    // 배달 완료 - 지원 안함
    @Override
    public void noteDelivered(String orderId) {
        throw new UnsupportedOperationException();
    }

    // 주문 조회
    @Override
    public Optional<Order> findOrder(String orderId) {
        Item item = table.getItem(new GetItemSpec()
                .withPrimaryKey(makePrimaryKey(orderId))
                .withConsistentRead(true));
        return Optional.ofNullable(item).map(this::toOrder);
    }

    // 주문 생성
    private Order toOrder(Item avs) {
        Order order = new Order(avs.getString("orderId"),
                avs.getString("consumerId"),
                OrderState.valueOf(avs.getString("orderStatus")),
                toLineItems2(avs.getList("lineItems")),
                null,
                avs.getLong("restaurantId"),
                avs.getString("restaurantName"));
        if (avs.hasAttribute("creationDate"))
            order.setCreationDate(new DateTime(avs.getLong("creationDate")));
        return order;
    }

    private List<OrderLineItem> toLineItems2(List<LinkedHashMap<String, Object>> lineItems) {
        return lineItems.stream().map(this::toLineItem2).collect(toList());
    }

    private OrderLineItem toLineItem2(LinkedHashMap<String, Object> attributeValue) {
        return new OrderLineItem((String) attributeValue.get("menuItemId"),
                                 (String) attributeValue.get("menuItemName"),
                                 new Money((String) attributeValue.get("price")),
                                 ((BigDecimal) attributeValue.get("quantity")).intValue());
    }
}
