package com.estore.order.core.event;

import com.estore.order.core.enumeration.OrderStatus;

import lombok.Value;

@Value
public class OrderApprovedEvent {
	private final String orderId;
	private final OrderStatus orderStatus = OrderStatus.APPROVED;
}