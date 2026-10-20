package org.emsi.EBank_Service;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.emsi.EBank_Service.entities.BankAccount;
import org.emsi.EBank_Service.enums.AccountType;
import org.emsi.EBank_Service.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EBankServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BankAccountRepository bankAccountRepository;

	@Test
	void listsSeededAccountsThroughDataRestWithIds() throws Exception {
		mockMvc.perform(get("/bankAccounts"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.bankAccounts.length()", greaterThanOrEqualTo(6)))
				.andExpect(jsonPath("$._embedded.bankAccounts[0].id").exists());
	}

	@Test
	void searchesAccountsByCustomerAndType() throws Exception {
		mockMvc.perform(get("/bankAccounts/search/byCustomer").param("customerId", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.bankAccounts", hasSize(2)));

		mockMvc.perform(get("/bankAccounts/search/byType").param("type", "SAVING_ACCOUNT"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.bankAccounts[*].type", everyItem(is("SAVING_ACCOUNT"))));
	}

	@Test
	void rejectsInvalidAccountThroughDataRest() throws Exception {
		mockMvc.perform(post("/bankAccounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"balance\":-10,\"currency\":\"\",\"type\":\"CURRENT_ACCOUNT\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void managesAccountLifecycleThroughApi() throws Exception {
		mockMvc.perform(post("/api/bankAccounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"balance\":1500,\"currency\":\"eur\",\"type\":\"CURRENT_ACCOUNT\",\"customerId\":1}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.createdAt").exists())
				.andExpect(jsonPath("$.currency").value("EUR"));

		String id = bankAccountRepository.findByCustomerId(1L).stream()
				.filter(account -> "EUR".equals(account.getCurrency()))
				.map(BankAccount::getId)
				.findFirst().orElseThrow();

		mockMvc.perform(put("/api/bankAccounts/{id}", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"balance\":2000,\"currency\":\"EUR\",\"type\":\"SAVING_ACCOUNT\",\"customerId\":1}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.balance").value(2000.0))
				.andExpect(jsonPath("$.type").value(AccountType.SAVING_ACCOUNT.name()));

		mockMvc.perform(delete("/api/bankAccounts/{id}", id))
				.andExpect(status().isNoContent());

		mockMvc.perform(get("/api/bankAccounts/{id}", id))
				.andExpect(status().isNotFound());
	}

	@Test
	void rejectsInvalidAccountThroughApi() throws Exception {
		mockMvc.perform(post("/api/bankAccounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"balance\":-1,\"currency\":\"MADX\",\"type\":\"CURRENT_ACCOUNT\"}"))
				.andExpect(status().isBadRequest());
	}
}
