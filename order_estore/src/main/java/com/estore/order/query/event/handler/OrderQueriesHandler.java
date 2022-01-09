package com.estore.order.query.event.handler;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import com.estore.order.core.data.OrderEntity;
import com.estore.order.core.data.OrderRepository;
import com.estore.order.core.model.OrderSummary;
import com.estore.order.query.FindOrderQuery;

@Component
public class OrderQueriesHandler {

	OrderRepository orderRepository;

	public OrderQueriesHandler(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@QueryHandler
	public OrderSummary findOrder(FindOrderQuery findOrderQuery) {
		OrderEntity orderEntity = orderRepository.findByOrderId(findOrderQuery.getOrderId());
		return new OrderSummary(orderEntity.getOrderId(), orderEntity.getOrderStatus(), "");
	}
}