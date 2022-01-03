package com.estore.product.command.rest;

import java.util.UUID;

import javax.validation.Valid;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.estore.product.command.CreateProductCommand;
import com.estore.product.command.model.CreateProductRestModel;

@RestController
@RequestMapping("/products")
public class ProductCommandController {

	private final CommandGateway commandGateway;

	@Autowired
	public ProductCommandController(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	@PostMapping
	public @ResponseBody ResponseEntity<Void> createProduct(
			@Valid @RequestBody CreateProductRestModel createProductRestModel) {
		CreateProductCommand createProductCommand = CreateProductCommand.builder()
				.price(createProductRestModel.getPrice()).quantity(createProductRestModel.getQuantity())
				.title(createProductRestModel.getTitle()).productId(UUID.randomUUID().toString()).build();

		commandGateway.sendAndWait(createProductCommand);

		return ResponseEntity.noContent().build();
	}
}