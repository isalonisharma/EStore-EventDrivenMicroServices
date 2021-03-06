package com.estore.product.command.aggregate;

import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.estore.core.command.CancelProductReservationCommand;
import com.estore.core.command.ReserveProductCommand;
import com.estore.core.event.ProductReservationCancelledEvent;
import com.estore.core.event.ProductReservedEvent;
import com.estore.product.command.CreateProductCommand;
import com.estore.product.core.event.ProductCreatedEvent;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Aggregate
@NoArgsConstructor
public class ProductAggregate {
	@AggregateIdentifier
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;

	@CommandHandler
	public ProductAggregate(CreateProductCommand createProductCommand) {
		// Validate Create Product Command

		if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Price cannot be less or equal than zero");
		}

		if (createProductCommand.getTitle() == null || createProductCommand.getTitle().isEmpty()) {
			throw new IllegalArgumentException("Title cannot be empty");
		}

		ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
		BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

		AggregateLifecycle.apply(productCreatedEvent);
	}

	@CommandHandler
	public void handle(ReserveProductCommand reserveProductCommand) {
		if (quantity < reserveProductCommand.getQuantity()) {
			throw new IllegalArgumentException("Insufficient number of items in stock");
		}

		ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
				.orderId(reserveProductCommand.getOrderId()).productId(reserveProductCommand.getProductId())
				.quantity(reserveProductCommand.getQuantity()).userId(reserveProductCommand.getUserId()).build();

		AggregateLifecycle.apply(productReservedEvent);
	}

	@EventSourcingHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		this.setProductId(productCreatedEvent.getProductId());
		this.setPrice(productCreatedEvent.getPrice());
		this.setTitle(productCreatedEvent.getTitle());
		this.setQuantity(productCreatedEvent.getQuantity());
	}

	@EventSourcingHandler
	public void on(ProductReservedEvent productReservedEvent) {
		this.quantity -= productReservedEvent.getQuantity();
	}

	@CommandHandler
	public void handle(CancelProductReservationCommand cancelProductReservationCommand) {
		ProductReservationCancelledEvent productReservationCancelledEvent = ProductReservationCancelledEvent.builder()
				.orderId(cancelProductReservationCommand.getOrderId())
				.productId(cancelProductReservationCommand.getProductId())
				.quantity(cancelProductReservationCommand.getQuantity())
				.reason(cancelProductReservationCommand.getReason()).userId(cancelProductReservationCommand.getUserId())
				.build();

		AggregateLifecycle.apply(productReservationCancelledEvent);
	}

	@EventSourcingHandler
	public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {
		this.quantity += productReservationCancelledEvent.getQuantity();
	}
}
