package com.bloknoma.ftgo.orderservice.domain;

import com.bloknoma.ftgo.common.CommonConfiguration;
import com.bloknoma.ftgo.orderservice.domain.repository.OrderRepository;
import com.bloknoma.ftgo.orderservice.domain.repository.RestaurantRepository;
import com.bloknoma.ftgo.orderservice.sagaparticipants.AccountingServiceProxy;
import com.bloknoma.ftgo.orderservice.sagaparticipants.ConsumerServiceProxy;
import com.bloknoma.ftgo.orderservice.sagaparticipants.KitchenServiceProxy;
import com.bloknoma.ftgo.orderservice.sagaparticipants.OrderServiceProxy;
import com.bloknoma.ftgo.orderservice.sagas.cancelorder.CancelOrderSaga;
import com.bloknoma.ftgo.orderservice.sagas.cancelorder.CancelOrderSagaData;
import com.bloknoma.ftgo.orderservice.sagas.createorder.CreateOrderSaga;
import com.bloknoma.ftgo.orderservice.sagas.createorder.CreateOrderSagaState;
import com.bloknoma.ftgo.orderservice.sagas.reviseorder.ReviseOrderSaga;
import com.bloknoma.ftgo.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import io.eventuate.tram.events.publisher.TramEventsPublisherConfiguration;
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer;
import io.eventuate.tram.sagas.orchestration.SagaManager;
import io.eventuate.tram.sagas.orchestration.SagaManagerImpl;
import io.eventuate.tram.sagas.orchestration.SagaOrchestratorConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@Configuration
@Import({TramEventsPublisherConfiguration.class, SagaOrchestratorConfiguration.class, CommonConfiguration.class})
public class OrderServiceConfiguration {
    // TODO move to framework

    @Bean
    public SagaCommandProducer sagaCommandProducer() {
        return new SagaCommandProducer();
    }

    @Bean
    public OrderService orderService(RestaurantRepository restaurantRepository, OrderRepository orderRepository, DomainEventPublisher eventPublisher,
                                     SagaManager<CreateOrderSagaState> createOrderSagaManager, SagaManager<CancelOrderSagaData> cancelOrderSagaManager,
                                     SagaManager<ReviseOrderSagaData> reviseOrderSagaManager, OrderDomainEventPublisher orderAggregateEventPublisher, Optional<MeterRegistry> meterRegistry) {
        return new OrderService(orderRepository, eventPublisher, restaurantRepository, createOrderSagaManager, cancelOrderSagaManager, reviseOrderSagaManager, orderAggregateEventPublisher, meterRegistry);
    }

    @Bean
    public SagaManager<CreateOrderSagaState> createOrderSagaManager(CreateOrderSaga saga) {
        return new SagaManagerImpl<>(saga);
    }

    @Bean
    public CreateOrderSaga createOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService, KitchenServiceProxy kitchenService, AccountingServiceProxy accountingService) {
        return new CreateOrderSaga(orderService, consumerService, kitchenService, accountingService);
    }

    @Bean
    public SagaManager<CancelOrderSagaData> CancelOrderSagaManager(CancelOrderSaga saga) {
        return new SagaManagerImpl<>(saga);
    }

    @Bean
    public CancelOrderSaga cancelOrderSaga() {
        return new CancelOrderSaga();
    }

    @Bean
    public SagaManager<ReviseOrderSagaData> reviseOrderSagaManager(ReviseOrderSaga saga) {
        return new SagaManagerImpl<>(saga);
    }

    @Bean
    public ReviseOrderSaga reviseOrderSaga() {
        return new ReviseOrderSaga();
    }

    @Bean
    public OrderServiceProxy orderServiceProxy() {
        return new OrderServiceProxy();
    }

    @Bean
    public ConsumerServiceProxy consumerServiceProxy() {
        return new ConsumerServiceProxy();
    }

    @Bean
    public KitchenServiceProxy kitchenServiceProxy() {
        return new KitchenServiceProxy();
    }

    @Bean
    public AccountingServiceProxy accountingServiceProxy() {
        return new AccountingServiceProxy();
    }

    @Bean
    public OrderDomainEventPublisher orderAggregateEventPublisher(DomainEventPublisher eventPublisher) {
        return new OrderDomainEventPublisher(eventPublisher);
    }

    @Bean
    public MeterRegistryCustomizer meterRegistryCustomizer(@Value("${spring.application.name") String serviceName) {
        return registry -> registry.config().commonTags("service", serviceName);
    }
}
