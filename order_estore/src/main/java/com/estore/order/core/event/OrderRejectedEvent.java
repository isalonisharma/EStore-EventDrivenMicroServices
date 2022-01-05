package com.estore.order.core.event;

import com.estore.order.core.enumeration.OrderStatus;

import lombok.Value;

@Value
public class OrderRejectedEvent {
	private final String orderId;
	private final String reason;
	private final OrderStatus orderStatus = OrderStatus.REJECTED;
}