package edu.joyful.orderservice.command.api.model;

import edu.joyful.orderservice.command.api.model.command.CompleteOrderCommand;
import edu.joyful.orderservice.command.api.model.command.CreateOrderCommand;
import edu.joyful.orderservice.command.api.model.event.OrderCompletedEvent;
import edu.joyful.orderservice.command.api.model.event.OrderCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        // TODO: 28.02.2023 validate command

        final OrderCreatedEvent event = new OrderCreatedEvent();

        BeanUtils.copyProperties(command, event);

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handleCompleteOrder(CompleteOrderCommand command) {
        // TODO: 28.02.2023 validate command

        final OrderCompletedEvent event = OrderCompletedEvent.builder()
                .orderId(command.getOrderId())
                .orderStatus(command.getOrderStatus())
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        orderId = event.getOrderId();
        productId = event.getProductId();
        userId = event.getUserId();
        addressId = event.getAddressId();
        quantity = event.getQuantity();
        orderStatus = event.getOrderStatus();

    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {
        orderStatus = event.getOrderStatus();
    }
}
