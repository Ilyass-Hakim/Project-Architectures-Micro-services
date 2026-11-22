package org.emsi.EBank_Service;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.emsi.EBank_Service.clients.CustomerRestClient;
import org.emsi.EBank_Service.entities.BankAccount;
import org.emsi.EBank_Service.enums.AccountType;
import org.emsi.EBank_Service.model.Customer;
import org.emsi.EBank_Service.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

// Les tests tournent sans Eureka et sans Customer-Service :
// le client Feign est remplacé par un mock (sinon POST /api/bankAccounts renverrait 503).
@SpringBootTest(properties = "DISCOVERY_ENABLED=false")
@AutoConfigureMockMvc
class EBankServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BankAccountRepository bankAccountRepository;

	@MockitoBean
	private CustomerRestClient customerRestClient;

	@BeforeEach
	void mockCustomerService() {
		when(customerRestClient.findCustomerById(anyLong()))
				.thenAnswer(call -> new Customer(call.getArgument(0), "Mohamed", "mohamed@gmail.com"));
	}

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
				.andExpect(jsonPath("$.currency").value("EUR"))
				.andExpect(jsonPath("$.customer.name").value("Mohamed"));

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
