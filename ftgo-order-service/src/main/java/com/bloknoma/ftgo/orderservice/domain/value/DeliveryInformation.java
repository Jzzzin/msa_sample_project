package com.bloknoma.ftgo.orderservice.domain.value;

import javax.persistence.*;
import java.time.LocalDateTime;

// 배달 정보
@Access(AccessType.FIELD)
public class DeliveryInformation {

    private LocalDateTime deliveryTime;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "state", column = @Column(name = "delivery_state"))
    })
    private Address deliveryAddress;
}
