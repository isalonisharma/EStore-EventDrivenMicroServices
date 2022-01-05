package com.estore.order.query.event.handler;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.estore.order.core.data.OrderEntity;
import com.estore.order.core.data.OrderRepository;
import com.estore.order.core.event.OrderApprovedEvent;
import com.estore.order.core.event.OrderCreatedEvent;
import com.estore.order.core.event.OrderRejectedEvent;

@Component
@ProcessingGroup("order-group")
public class OrderEventsHandler {

	private final OrderRepository orderRepository;

	public OrderEventsHandler(OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@EventHandler
	public void on(OrderCreatedEvent event) throws Exception {
		OrderEntity orderEntity = new OrderEntity();
		BeanUtils.copyProperties(event, orderEntity);

		this.orderRepository.save(orderEntity);
	}
	
	@EventHandler
    public void on(OrderApprovedEvent orderApprovedEvent) {
    	OrderEntity orderEntity = orderRepository.findByOrderId(orderApprovedEvent.getOrderId());
   
    	if(orderEntity == null) {
    		return;
    	}
    	
    	orderEntity.setOrderStatus(orderApprovedEvent.getOrderStatus());
    	orderRepository.save(orderEntity);
    }
	
	@EventHandler
    public void on(OrderRejectedEvent orderRejectedEvent) {
    	OrderEntity orderEntity = orderRepository.findByOrderId(orderRejectedEvent.getOrderId());
    	orderEntity.setOrderStatus(orderRejectedEvent.getOrderStatus());
    	orderRepository.save(orderEntity);
    }
}