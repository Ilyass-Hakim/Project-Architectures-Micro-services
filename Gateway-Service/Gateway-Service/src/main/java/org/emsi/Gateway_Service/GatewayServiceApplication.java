package org.emsi.Gateway_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway : tous les clients (Postman, front, chatbot...) passent par ici (port 8888).
 * Les routes sont déclarées dans application.yml.
 */
@SpringBootApplication
public class GatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}

}
