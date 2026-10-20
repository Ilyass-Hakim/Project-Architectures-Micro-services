package org.emsi.Customer_Service;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void listsSeededCustomersWithIds() throws Exception {
		mockMvc.perform(get("/customers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.customers.length()", greaterThanOrEqualTo(3)))
				.andExpect(jsonPath("$._embedded.customers[0].id").exists());
	}

	@Test
	void createsCustomer() throws Exception {
		mockMvc.perform(post("/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Sara\",\"email\":\"sara@gmail.com\"}"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/customers/search/byEmail").param("email", "sara@gmail.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Sara"));
	}

	@Test
	void rejectsInvalidCustomer() throws Exception {
		mockMvc.perform(post("/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"\",\"email\":\"not-an-email\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void searchesByName() throws Exception {
		mockMvc.perform(get("/customers/search/byName").param("kw", "ima"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.customers[0].email").value("imane@gmail.com"));
	}

	@Test
	void exposesFullCustomerProjection() throws Exception {
		mockMvc.perform(get("/customers/1").param("projection", "fullCustomer"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.email").exists());
	}
}
