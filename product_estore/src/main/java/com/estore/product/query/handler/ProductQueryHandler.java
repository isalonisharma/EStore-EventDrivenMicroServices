package com.estore.product.query.handler;

import java.util.ArrayList;
import java.util.List;

import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.estore.product.core.data.ProductEntity;
import com.estore.product.core.data.ProductRepository;
import com.estore.product.query.FindProductsQuery;
import com.estore.product.query.model.ProductRestModel;

@Component
public class ProductQueryHandler {
	private final ProductRepository productRepository;

	public ProductQueryHandler(ProductRepository productsRepository) {
		this.productRepository = productsRepository;
	}

	@QueryHandler
	public List<ProductRestModel> findProducts(FindProductsQuery query) {
		List<ProductRestModel> productsRest = new ArrayList<>();

		List<ProductEntity> storedProducts = productRepository.findAll();

		for (ProductEntity productEntity : storedProducts) {
			ProductRestModel productRestModel = new ProductRestModel();
			BeanUtils.copyProperties(productEntity, productRestModel);
			productsRest.add(productRestModel);
		}

		return productsRest;
	}
}