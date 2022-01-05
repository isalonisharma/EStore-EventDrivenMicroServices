package com.estore.order.saga;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.estore.core.command.ProcessPaymentCommand;
import com.estore.core.command.ReserveProductCommand;
import com.estore.core.event.PaymentProcessedEvent;
import com.estore.core.event.ProductReservedEvent;
import com.estore.core.model.User;
import com.estore.core.query.FetchUserPaymentDetailQuery;
import com.estore.order.command.ApproveOrderCommand;
import com.estore.order.core.event.OrderApprovedEvent;
import com.estore.order.core.event.OrderCreatedEvent;

@Saga
public class OrderSaga {
	/*
	 * because Saga is serialized, it is important to mark the component we are
	 * injecting with transient
	 */
	@Autowired
	private transient CommandGateway commandGateway;

	@Autowired
	private transient QueryGateway queryGateway;

	private static final Logger LOGGER = LoggerFactory.getLogger(OrderSaga.class);

	@StartSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderCreatedEvent orderCreatedEvent) {
		ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
				.orderId(orderCreatedEvent.getOrderId()).productId(orderCreatedEvent.getProductId())
				.quantity(orderCreatedEvent.getQuantity()).userId(orderCreatedEvent.getUserId()).build();

		LOGGER.info("OrderCreatedEvent handled for orderId: " + reserveProductCommand.getOrderId() + " and productId: "
				+ reserveProductCommand.getProductId());

		commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

			@Override
			public void onResult(CommandMessage<? extends ReserveProductCommand> commandMessage,
					CommandResultMessage<? extends Object> commandResultMessage) {

				if (commandResultMessage.isExceptional()) {
					// Start a compensating transaction

				}

			}

		});
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(ProductReservedEvent productReservedEvent) {
		// Process user payment
		LOGGER.info("ProductReserveddEvent is called for productId: " + productReservedEvent.getProductId()
				+ " and orderId: " + productReservedEvent.getOrderId());

		FetchUserPaymentDetailQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailQuery(
				productReservedEvent.getUserId());

		User userPaymentDetails = null;

		try {
			userPaymentDetails = queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class))
					.join();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			LOGGER.error("Unable to fecth payment details for orderId: " + productReservedEvent.getOrderId()
					+ ",  userId: " + productReservedEvent.getUserId());
			// Start compensating transaction
			return;
		}

		if (userPaymentDetails == null) {
			// Start compensating transaction
			return;
		}

		LOGGER.info("Successfully fetched user payment details for user " + userPaymentDetails.getFirstName());

		ProcessPaymentCommand proccessPaymentCommand = ProcessPaymentCommand.builder()
				.orderId(productReservedEvent.getOrderId()).paymentDetails(userPaymentDetails.getPaymentDetails())
				.paymentId(UUID.randomUUID().toString()).build();

		String result = null;
		try {
			result = commandGateway.sendAndWait(proccessPaymentCommand, 10, TimeUnit.SECONDS);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			// Start compensating transaction
			return;
		}

		if (result == null) {
			LOGGER.info("The ProcessPaymentCommand resulted in NULL. Initiating a compensating transaction");
			// Start compensating transaction
		}
	}

	@SagaEventHandler(associationProperty = "orderId")
	public void handle(PaymentProcessedEvent paymentProcessedEvent) {
		// Send an ApproveOrderCommand
		ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(paymentProcessedEvent.getOrderId());

		commandGateway.send(approveOrderCommand);
	}

	@EndSaga
	@SagaEventHandler(associationProperty = "orderId")
	public void handle(OrderApprovedEvent orderApprovedEvent) {
		LOGGER.info("Order is approved. Order Saga is complete for orderId: " + orderApprovedEvent.getOrderId());
		SagaLifecycle.end();
	}
}