package com.estore.payment.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import com.estore.core.command.ProcessPaymentCommand;
import com.estore.core.event.PaymentProcessedEvent;

import lombok.Data;

@Data
@Aggregate
public class PaymentAggregate {

	@AggregateIdentifier
	private String paymentId;

	private String orderId;

	public PaymentAggregate() {
	}

	@CommandHandler
	public PaymentAggregate(ProcessPaymentCommand processPaymentCommand) {
		if (processPaymentCommand.getPaymentDetails() == null) {
			throw new IllegalArgumentException("Missing payment details");
		}

		if (processPaymentCommand.getOrderId() == null) {
			throw new IllegalArgumentException("Missing orderId");
		}

		if (processPaymentCommand.getPaymentId() == null) {
			throw new IllegalArgumentException("Missing paymentId");
		}

		AggregateLifecycle.apply(
				new PaymentProcessedEvent(processPaymentCommand.getOrderId(), processPaymentCommand.getPaymentId()));
	}

	@EventSourcingHandler
	protected void on(PaymentProcessedEvent paymentProcessedEvent) {
		this.setPaymentId(paymentProcessedEvent.getPaymentId());
		this.setOrderId(paymentProcessedEvent.getOrderId());
	}
}