package com.bloknoma.ftgo.orderservice.sagaparticipants;

// order command - reject
public class RejectOrderCommand extends OrderCommand {

    private RejectOrderCommand() {
    }

    public RejectOrderCommand(long orderId) {
        super(orderId);
    }
}
