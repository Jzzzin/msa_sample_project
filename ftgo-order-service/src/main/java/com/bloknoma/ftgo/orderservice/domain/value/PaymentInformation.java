package com.bloknoma.ftgo.orderservice.domain.value;

import javax.persistence.Access;
import javax.persistence.AccessType;

// 결제 정보
@Access(AccessType.FIELD)
public class PaymentInformation {

    private String paymentToken;
}
