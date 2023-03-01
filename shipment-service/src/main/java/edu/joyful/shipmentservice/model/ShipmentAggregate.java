package edu.joyful.shipmentservice.model;

import edu.joyful.commonservice.api.shipment.command.ShipOrderCommand;
import edu.joyful.commonservice.api.shipment.event.OrderShippedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Slf4j
@Aggregate
public class ShipmentAggregate {

    @AggregateIdentifier
    private String shipmentId;
    private String orderId;
    private String shipmentStatus;

    public ShipmentAggregate() {
    }

    @CommandHandler
    public ShipmentAggregate(ShipOrderCommand command) {
        final OrderShippedEvent event = OrderShippedEvent.builder()
                .orderId(command.getOrderId())
                .shipmentId(command.getShipmentId())
                .shipmentStatus("COMPLETED")
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderShippedEvent event) {
        shipmentId = event.getShipmentId();
        orderId = event.getOrderId();
        shipmentStatus = event.getShipmentStatus();
    }
}