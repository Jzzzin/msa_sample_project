package contracts.messaging

org.springframework.cloud.contract.spec.Contract.make {
    label 'orderCreatedEvent' // 컨슈머 테스트에서 사용
    input {
        triggeredBy('orderCreated()') // 프로바이더 테스트에 의해 호출
    }

    outputMessage {
        sentTo('com.bloknoma.ftgo.orderservice.domain.Order')
        body('''{"orderDetails":{"lineItems":[{"quantity":5,"menuItemId":"1","name":"Chicken Vindaloo","price":"12.34","total":"61.70"}],"orderTotal":"61.70","restaurantId":1, "consumerId":1511300065921}, "restaurantName" : "Ajanta"}''')
        headers {
            header('event-aggregate-type', 'com.bloknoma.ftgo.orderservice.domain.Order')
            header('event-type', 'com.bloknoma.ftgo.orderservice.api.events.OrderCreatedEvent')
            header('event-aggregate-id', '99') // Matches OrderDetailsMother.ORDER_ID
        }
    }
}
