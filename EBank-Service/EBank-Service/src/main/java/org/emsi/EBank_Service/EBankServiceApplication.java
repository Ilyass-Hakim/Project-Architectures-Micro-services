package org.emsi.EBank_Service;

import java.util.List;

import org.emsi.EBank_Service.entities.BankAccount;
import org.emsi.EBank_Service.enums.AccountType;
import org.emsi.EBank_Service.repository.BankAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EBankServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EBankServiceApplication.class, args);
	}

	@Bean
	@ConditionalOnProperty(name = "ebank.seed-data", havingValue = "true", matchIfMissing = true)
	CommandLineRunner seedBankAccounts(BankAccountRepository bankAccountRepository) {
		return args -> {
			if (bankAccountRepository.count() > 0) return;
			// Customer ids 1..3 match the customers seeded by Customer-Service
			List.of(1L, 2L, 3L).forEach(customerId -> bankAccountRepository.saveAll(List.of(
					BankAccount.builder().balance(5000.0 * customerId).currency("MAD")
							.type(AccountType.CURRENT_ACCOUNT).customerId(customerId).build(),
					BankAccount.builder().balance(12000.0 * customerId).currency("MAD")
							.type(AccountType.SAVING_ACCOUNT).customerId(customerId).build()
			)));
			bankAccountRepository.findAll().forEach(System.out::println);
		};
	}

}
