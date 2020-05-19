package com.bloknoma.ftgo.cqrs.orderhistory.dynamodb;

import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

import java.util.HashMap;

// 이벤트 정보
public class SourceEvent {

    String aggregateType;
    String aggregateId;
    String eventId;

    public SourceEvent(String aggregateType, String aggregateId, String eventId) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventId = eventId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    // 중복 이벤트 검증
    public UpdateItemSpec addDuplicateDetection(UpdateItemSpec spec) {
        HashMap<String, String> nameMap = spec.getNameMap() == null ? new HashMap<>() : new HashMap<>(spec.getNameMap());
        nameMap.put("#duplicateDetection", "events." + aggregateType + aggregateId);
        HashMap<String, Object> valueMap = new HashMap<>(spec.getValueMap());
        valueMap.put(":eventId", eventId);
        return spec.withUpdateExpression(String.format("%s, #duplicateDetection = :eventId", spec.getUpdateExpression())) // 업데이트 표현식
                .withNameMap(nameMap) // 업데이트 표현식 속성
                .withValueMap(valueMap) // 업데이트 표현식 값
                .withConditionExpression(Expressions.and(spec.getConditionExpression(), "attribute_not_exists(#duplicateDetection) OR #duplicateDetection < :eventId")); // 조건부 표현식
    }

}
