package edu.joyful.orderservice.command.api.service.saga;

import edu.joyful.commonservice.api.payment.command.ValidatePaymentCommand;
import edu.joyful.commonservice.api.payment.event.PaymentProcessedEvent;
import edu.joyful.commonservice.api.shipment.command.ShipOrderCommand;
import edu.joyful.commonservice.api.shipment.event.OrderShippedEvent;
import edu.joyful.commonservice.api.user.UserDto;
import edu.joyful.commonservice.api.user.query.GetUserPaymentDetailsQuery;
import edu.joyful.orderservice.command.api.model.command.CompleteOrderCommand;
import edu.joyful.orderservice.command.api.model.event.OrderCompletedEvent;
import edu.joyful.orderservice.command.api.model.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;

import static java.util.UUID.randomUUID;

@Slf4j
@Saga
@RequiredArgsConstructor
public class OrderProcessingSaga {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent in SAGA for orderId: {}", event.getOrderId());

        final GetUserPaymentDetailsQuery userQuery = GetUserPaymentDetailsQuery.builder()
                .userId(event.getUserId())
                .build();

        UserDto userDto = null;
        try {
            userDto = queryGateway.query(userQuery, ResponseTypes.instanceOf(UserDto.class))
                    .join();
        } catch (Exception e) {
            log.error(e.getMessage());
            // TODO: 01.03.2023 start compensation transaction for order command
        }

        final ValidatePaymentCommand paymentCommand = ValidatePaymentCommand.builder()
                .orderId(event.getOrderId())
                .paymentId(randomUUID().toString())
                .cardDetails(userDto.getCardDetails())
                .build();

        commandGateway.sendAndWait(paymentCommand);

        // TODO: 01.03.2023 handle ship command in shipment handler

        // TODO: 28.02.2023 complete order command

        // TODO: 28.02.2023 send invoice command
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("PaymentProcessedEvent in SAGA for orderId: {}", event.getOrderId());

        final ShipOrderCommand shipmentCommand = ShipOrderCommand.builder()
                .orderId(event.getOrderId())
                .shipmentId(randomUUID().toString())
                .build();

        commandGateway.sendAndWait(shipmentCommand);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handleOrderShipped(OrderShippedEvent event) {
        log.info("OrderShippedEvent in SAGA for orderId: {}", event.getOrderId());

        final CompleteOrderCommand orderCommand = CompleteOrderCommand.builder()
                .orderId(event.getOrderId())
                .orderStatus("APPROVED")
                .build();

        commandGateway.sendAndWait(orderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("OrderCompletedEvent in SAGA for orderId: {}", event.getOrderId());
    }
}
