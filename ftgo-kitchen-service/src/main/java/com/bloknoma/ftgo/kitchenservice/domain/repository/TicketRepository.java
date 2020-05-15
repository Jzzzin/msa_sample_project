package com.bloknoma.ftgo.kitchenservice.domain.repository;

import com.bloknoma.ftgo.kitchenservice.domain.Ticket;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
}
