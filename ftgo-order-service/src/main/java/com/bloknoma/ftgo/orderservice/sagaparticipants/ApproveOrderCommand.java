package com.bloknoma.ftgo.orderservice.sagaparticipants;

// order command - approve
public class ApproveOrderCommand extends OrderCommand {

    private ApproveOrderCommand() {
    }

    public ApproveOrderCommand(long orderId) {
        super(orderId);
    }
}
