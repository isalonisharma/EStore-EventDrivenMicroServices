package com.estore.order.command.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.estore.order.command.CreateOrderCommand;
import com.estore.order.core.enumeration.OrderStatus;
import com.estore.order.core.event.OrderCreatedEvent;

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
}