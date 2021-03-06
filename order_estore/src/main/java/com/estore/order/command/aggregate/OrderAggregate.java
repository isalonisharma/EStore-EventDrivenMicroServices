package com.estore.order.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.estore.order.command.ApproveOrderCommand;
import com.estore.order.command.CreateOrderCommand;
import com.estore.order.command.RejectOrderCommand;
import com.estore.order.core.enumeration.OrderStatus;
import com.estore.order.core.event.OrderApprovedEvent;
import com.estore.order.core.event.OrderCreatedEvent;
import com.estore.order.core.event.OrderRejectedEvent;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Aggregate
@NoArgsConstructor
public class OrderAggregate {

	@AggregateIdentifier
	private String orderId;
	private String productId;
	private String userId;
	private int quantity;
	private String addressId;
	private OrderStatus orderStatus;

	@CommandHandler
	public OrderAggregate(CreateOrderCommand createOrderCommand) {
		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
		BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);

		AggregateLifecycle.apply(orderCreatedEvent);
	}

	@EventSourcingHandler
	public void on(OrderCreatedEvent orderCreatedEvent) throws Exception {
		this.setOrderId(orderCreatedEvent.getOrderId());
		this.setProductId(orderCreatedEvent.getProductId());
		this.setUserId(orderCreatedEvent.getUserId());
		this.setAddressId(orderCreatedEvent.getAddressId());
		this.setQuantity(orderCreatedEvent.getQuantity());
		this.setOrderStatus(orderCreatedEvent.getOrderStatus());
	}

	@CommandHandler
	public void handle(ApproveOrderCommand approveOrderCommand) {
		// Create and publish the OrderApprovedEvent
		OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(approveOrderCommand.getOrderId());

		AggregateLifecycle.apply(orderApprovedEvent);
	}

	@EventSourcingHandler
	public void on(OrderApprovedEvent orderApprovedEvent) {
		this.orderStatus = orderApprovedEvent.getOrderStatus();
	}

	@CommandHandler
	public void handle(RejectOrderCommand rejectOrderCommand) {
		OrderRejectedEvent orderRejectedEvent = new OrderRejectedEvent(rejectOrderCommand.getOrderId(),
				rejectOrderCommand.getReason());

		AggregateLifecycle.apply(orderRejectedEvent);
	}

	@EventSourcingHandler
	public void on(OrderRejectedEvent orderRejectedEvent) {
		this.orderStatus = orderRejectedEvent.getOrderStatus();
	}
}