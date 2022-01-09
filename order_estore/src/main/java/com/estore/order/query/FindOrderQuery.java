package com.estore.order.query;

import lombok.Value;

@Value
public class FindOrderQuery {
	private final String orderId;
}