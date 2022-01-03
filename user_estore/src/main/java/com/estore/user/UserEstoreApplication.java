package com.estore.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class UserEstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserEstoreApplication.class, args);
	}
}