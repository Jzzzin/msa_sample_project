package com.bloknoma.ftgo.cqrs.orderhistory.dynamodb;

import java.util.HashMap;
import java.util.Map;

// map
public class Maps {

    private final Map<String, Object> map;

    public Maps() {
        this.map = new HashMap<>();
    }

    public Maps add(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public Map<String, Object> map() {
        return map;
    }
}
