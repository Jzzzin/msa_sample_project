package com.bloknoma.ftgo.kitchenservice.domain;

import com.bloknoma.ftgo.common.NotYetImplementedException;
import com.bloknoma.ftgo.common.UnsupportedStateTransitionException;
import com.bloknoma.ftgo.kitchenservice.api.TicketDetails;
import com.bloknoma.ftgo.kitchenservice.api.TicketLineItem;
import com.bloknoma.ftgo.kitchenservice.domain.event.*;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Entity
@Table(name = "tickets")
@Access(AccessType.FIELD)
public class Ticket {

    @Id
    private Long id;

    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    private TicketState state;

    private TicketState previousState;


    @ElementCollection
    @CollectionTable(name = "ticket_line_items")
    private List<TicketLineItem> lineItems;

    private LocalDateTime readyBy;
    private LocalDateTime acceptTime;
    private LocalDateTime preparingTime;
    private LocalDateTime pickedUpTime;
    private LocalDateTime readyForPickupTime;

    private Ticket() {
    }

    public Ticket(long restaurantId, Long id, TicketDetails details) {
        this.restaurantId = restaurantId;
        this.id = id;
        this.state = TicketState.CREATE_PENDING;
        this.lineItems = details.getLineItems();
    }

    public Long getId() {
        return id;
    }

    // 티켓 생성
    public static ResultWithDomainEvents<Ticket, TicketDomainEvent> create(long restaurantId, Long id, TicketDetails details) {
        return new ResultWithDomainEvents<>(new Ticket(restaurantId, id, details));
    }

    // 티켓 생성 확인
    public List<TicketDomainEvent> confirmCreate() {
        switch (state) {
            case CREATE_PENDING:
                state = TicketState.AWAITING_ACCEPTANCE;
                return singletonList(new TicketCreatedEvent(id, new TicketDetails()));
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 생성 취소
    public List<TicketDomainEvent> cancelCreate() {
        throw new NotYetImplementedException();
    }

    // 티켓 수락
    public List<TicketDomainEvent> accept(LocalDateTime readyBy) {
        switch (state) {
            case AWAITING_ACCEPTANCE:
                // Verify that readyBy is in the future
                this.acceptTime = LocalDateTime.now();
                if (!acceptTime.isBefore(readyBy))
                    throw new IllegalArgumentException("readyBy is not in the future");
                this.readyBy = readyBy;
                state = TicketState.ACCEPTED;
                return singletonList(new TicketAcceptedEvent(readyBy));
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // TODO reject()

    // 티켓 준비 시작
    public List<TicketDomainEvent> preparing() {
        switch (state) {
            case ACCEPTED:
                this.state = TicketState.PREPARING;
                this.preparingTime = LocalDateTime.now();
                return singletonList(new TicketPreparationStartedEvent());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 준비 완료
    public List<TicketDomainEvent> readyForPickup() {
        switch (state) {
            case PREPARING:
                this.state = TicketState.READY_FOR_PICKUP;
                this.readyForPickupTime = LocalDateTime.now();
                return singletonList(new TicketPreparationCompletedEvent());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 픽업
    public List<TicketDomainEvent> puckedUp() {
        switch (state) {
            case READY_FOR_PICKUP:
                this.state = TicketState.PICKED_UP;
                this.pickedUpTime = LocalDateTime.now();
                return singletonList(new TicketPickedUpEvent());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 취소
    public List<TicketDomainEvent> cancel() {
        switch (state) {
            case AWAITING_ACCEPTANCE:
            case ACCEPTED:
                this.previousState = state;
                this.state = TicketState.CANCEL_PENDING;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 취소 확인
    public List<TicketDomainEvent> confirmCancel() {
        switch (state) {
            case CANCEL_PENDING:
                this.state = TicketState.CANCELLED;
                return singletonList(new TicketCancelled());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 취소 언두
    public List<TicketDomainEvent> undoCancel() {
        switch (state) {
            case CANCEL_PENDING:
                this.state = this.previousState;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 목록 수량 변경
    public void changeLineItemQuantity() {
        switch (state) {
            case AWAITING_ACCEPTANCE:
                // TODO
                break;
            case PREPARING:
                // TODO - too late
                break;
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 변경
    public List<TicketDomainEvent> beginReviseOrder(Map<String, Integer> revisedLineItemQuantities) {
        switch (state) {
            case AWAITING_ACCEPTANCE:
            case ACCEPTED:
                this.previousState = state;
                this.state = TicketState.REVISION_PENDING;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 변경 언두 - 그냥 이전 상태로 돌아감
    public List<TicketDomainEvent> undoBeginReviseOrder() {
        switch (state) {
            case REVISION_PENDING:
                this.state = this.previousState;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    // 티켓 변경 확인 - 이전 상태로 돌아가면서 이벤트 발생
    public List<TicketDomainEvent> confirmReviseTicket(Map<String, Integer> revisedLineItemQuntities) {
        switch (state) {
            case REVISION_PENDING:
                this.state = this.previousState;
                return singletonList(new TicketRevised());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }
}
