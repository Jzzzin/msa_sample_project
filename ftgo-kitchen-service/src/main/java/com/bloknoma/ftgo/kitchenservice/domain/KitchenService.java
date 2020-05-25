package com.bloknoma.ftgo.kitchenservice.domain;

import com.bloknoma.ftgo.kitchenservice.api.TicketDetails;
import com.bloknoma.ftgo.kitchenservice.domain.event.TicketDomainEvent;
import com.bloknoma.ftgo.kitchenservice.domain.exception.RestaurantNotFoundException;
import com.bloknoma.ftgo.kitchenservice.domain.exception.TicketNotFoundException;
import com.bloknoma.ftgo.kitchenservice.domain.repository.RestaurantRepository;
import com.bloknoma.ftgo.kitchenservice.domain.repository.TicketRepository;
import com.bloknoma.ftgo.restaurantservice.events.RestaurantMenu;
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Transactional
public class KitchenService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketDomainEventPublisher domainEventPublisher;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // 레스토랑 추가
    public void createMenu(long id, RestaurantMenu menu) {
        Restaurant restaurant = new Restaurant(id, menu.getMenuItems());
        restaurantRepository.save(restaurant);
    }

    // 메뉴 변경 - 지원 안함?
    public void reviseMenu(long id, RestaurantMenu revisedMenu) {
        // 레스토랑을 왜 ticketId 로 찾지 ? - 변경 했음
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
        restaurant.reviseMenu(revisedMenu);
    }

    // 티켓 생성
    public Ticket createTicket(long restaurantId, Long ticketId, TicketDetails ticketDetails) {
        // 티켓 생성
        ResultWithDomainEvents<Ticket, TicketDomainEvent> rwe = Ticket.create(restaurantId, ticketId, ticketDetails);
        // 티켓 저장
        ticketRepository.save(rwe.result);
        // 이벤트 발행
        domainEventPublisher.publish(rwe.result, rwe.events);
        return rwe.result;
    }

    // 티켓 생성 확인
    public void confirmCreateTicket(Long ticketId) {
        // 티켓 조회
        Ticket ro = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // 티켓 생성 확인
        List<TicketDomainEvent> events = ro.confirmCreate();
        // 이벤트 발행
        domainEventPublisher.publish(ro, events);
    }

    // 티켓 생성 취소
    public void cancelCreateTicket(Long ticketId) {
        // 티켓 조회
        Ticket ro = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // 티켓 생성 취소
        List<TicketDomainEvent> events = ro.cancelCreate();
        // 이벤트 발행
        domainEventPublisher.publish(ro, events);
    }

    // 티켓 수락
    public void accept(long ticketId, LocalDateTime readyBy) {
        // 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // 티켓 수락
        List<TicketDomainEvent> events = ticket.accept(readyBy);
        // 이벤트 발행
        domainEventPublisher.publish(ticket, events);
    }

    // 티켓 취소
    public void cancelTicket(long restaurantId, long ticketId) {
        // 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // TODO - verify restaurant id - 레스토랑을 조회 해올 필요 까지는 없을 것 같은뎀 / 레스토랑 ID를 안넘겨서 에러남
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        // 티켓 취소
        List<TicketDomainEvent> events = ticket.cancel();
        // 이벤트 발행
        domainEventPublisher.publish(ticket, events);
    }

    // 티켓 취소 확인
    public void confirmCancelTicket(long restaurantId, long ticketId) {
        // 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // TODO - verify restaurant id - 레스토랑을 조회 해올 필요 까지는 없을 것 같은뎀 / 레스토랑 ID를 안넘겨서 에러남
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        // 티켓 취소 확인
        List<TicketDomainEvent> events = ticket.confirmCancel();
        // 이벤트 발행
        domainEventPublisher.publish(ticket, events);
    }

    // 티켓 취소 언두
    public void undoCancel(long restaurantId, long ticketId) {
        // 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // TODO - verify restaurant id - 레스토랑을 조회 해올 필요 까지는 없을 것 같은뎀 / 레스토랑 ID를 안넘겨서 에러남
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        // 티켓 취소 언두
        List<TicketDomainEvent> events = ticket.undoCancel();
        // 이벤트 발행
        domainEventPublisher.publish(ticket, events);
    }

    // 티켓 변경
    public void beginReviseOrder(long restaurantId, Long ticketId, Map<String, Integer> revisedLineItemQuantities) {
        // 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // TODO - verify restaurant id - 레스토랑을 조회 해올 필요 까지는 없을 것 같은뎀 / 레스토랑 ID를 안넘겨서 에러남
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        // 티켓 변경
        List<TicketDomainEvent> events = ticket.beginReviseOrder(revisedLineItemQuantities);
        // 이벤트 발행
        domainEventPublisher.publish(ticket, events);
    }

    // 티켓 변경 언두
    public void undoBeginReviseOrder(long restaurantId, Long ticketId) {
        // 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // TODO - verify restaurant id - 레스토랑을 조회 해올 필요 까지는 없을 것 같은뎀 / 레스토랑 ID를 안넘겨서 에러남
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        // 티켓 변경 언두
        List<TicketDomainEvent> events = ticket.undoBeginReviseOrder();
        // 이벤트 발행
        domainEventPublisher.publish(ticket, events);
    }

    // 티켓 변경 확인
    public void confirmReviseTicket(long restaurantId, long ticketId, Map<String, Integer> revisedLineItemQuantities) {
        // 티켓 조회
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        // TODO - verify restaurant id - 레스토랑을 조회 해올 필요 까지는 없을 것 같은뎀 / 레스토랑 ID를 안넘겨서 에러남
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        // 티켓 변경 확인
        List<TicketDomainEvent> events = ticket.confirmReviseTicket(revisedLineItemQuantities);
        // 이벤트 발행
        domainEventPublisher.publish(ticket, events);
    }

    // ...
}
