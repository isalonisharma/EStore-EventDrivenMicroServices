package com.estore.product.query.rest;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estore.product.query.FindProductsQuery;
import com.estore.product.query.model.ProductRestModel;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

	@Autowired
	QueryGateway queryGateway;

	@GetMapping
	public List<ProductRestModel> getProducts() {

		FindProductsQuery findProductsQuery = new FindProductsQuery();

		return queryGateway.query(findProductsQuery, ResponseTypes.multipleInstancesOf(ProductRestModel.class)).join();
	}
}