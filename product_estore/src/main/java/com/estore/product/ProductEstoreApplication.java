package com.estore.product;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

import com.estore.product.command.interceptor.CreateProductCommandInterceptor;
import com.estore.product.core.errorhandler.ProductEventErrorHandler;

@EnableDiscoveryClient
@SpringBootApplication
public class ProductEstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductEstoreApplication.class, args);
	}

	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus commandBus) {
		commandBus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));
	}

	@Autowired
	public void configure(EventProcessingConfigurer eventProcessingConfigurer) {
		eventProcessingConfigurer.registerListenerInvocationErrorHandler("product-group",
				conf -> new ProductEventErrorHandler());
	}
}