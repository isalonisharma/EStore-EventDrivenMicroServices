package com.estore.order.core.model;

import com.estore.order.core.enumeration.OrderStatus;

import lombok.Value;

@Value
public class OrderSummary {
	private final String orderId;
	private final OrderStatus orderStatus;
	private final String message;
}