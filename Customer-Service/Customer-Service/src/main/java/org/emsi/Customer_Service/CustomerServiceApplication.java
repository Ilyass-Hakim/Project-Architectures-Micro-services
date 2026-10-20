package org.emsi.Customer_Service;

import java.util.List;

import org.emsi.Customer_Service.entities.Customer;
import org.emsi.Customer_Service.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(name = "customer.seed-data", havingValue = "true", matchIfMissing = true)
	CommandLineRunner seedCustomers(CustomerRepository customerRepository) {
		return args -> {
			if (customerRepository.count() > 0) return;
			customerRepository.saveAll(List.of(
					Customer.builder().name("Mohamed").email("mohamed@gmail.com").build(),
					Customer.builder().name("Imane").email("imane@gmail.com").build(),
					Customer.builder().name("Yassine").email("yassine@gmail.com").build()
			));
			customerRepository.findAll().forEach(System.out::println);
		};
	}

}
