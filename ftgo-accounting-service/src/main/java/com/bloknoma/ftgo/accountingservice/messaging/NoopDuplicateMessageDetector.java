package com.bloknoma.ftgo.accountingservice.messaging;

import io.eventuate.tram.consumer.common.DuplicateMessageDetector;
import io.eventuate.tram.consumer.common.SubscriberIdAndMessage;

// 중복 메시지 체크
public class NoopDuplicateMessageDetector implements DuplicateMessageDetector {

    @Override
    public boolean isDuplicate(String consumerId, String messageId) {
        return false;
    }

    @Override
    public void doWithMessage(SubscriberIdAndMessage subscriberIdAndMessage, Runnable callback) {
        callback.run();
    }
}
