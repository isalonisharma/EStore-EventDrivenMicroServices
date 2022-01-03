package com.estore.product.command.handler;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import com.estore.product.core.data.ProductLookupEntity;
import com.estore.product.core.data.ProductLookupRepository;
import com.estore.product.core.event.ProductCreatedEvent;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventHandler {
	private final ProductLookupRepository productLookupRepository;

	public ProductLookupEventHandler(ProductLookupRepository productLookupRepository) {
		this.productLookupRepository = productLookupRepository;
	}

	@EventHandler
	public void on(ProductCreatedEvent event) {
		ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getProductId(), event.getTitle());
		productLookupRepository.save(productLookupEntity);
	}
}