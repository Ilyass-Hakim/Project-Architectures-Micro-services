package org.emsi.AI_Agent_Service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.emsi.AI_Agent_Service.agents.EbankAiAgent;
import org.emsi.AI_Agent_Service.web.AiAgentRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Teste le client REST du chatbot (l'agent est simulé : pas d'appel à OpenAI).
 */
@WebMvcTest(controllers = AiAgentRestController.class,
		properties = { "DISCOVERY_ENABLED=false", "spring.ai.openai.api-key=test-key" })
class AiAgentRestControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private EbankAiAgent agent;

	@Test
	void chatReturnsAgentAnswerInUtf8() throws Exception {
		when(agent.chat("C1", "Combien de clients ?")).thenReturn("La banque a 3 clients, dont Imane et Mohamed.");

		mockMvc.perform(get("/chat").param("query", "Combien de clients ?"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("text/plain;charset=UTF-8"))
				.andExpect(content().string("La banque a 3 clients, dont Imane et Mohamed."));
	}

	@Test
	void chatUsesTheGivenConversationId() throws Exception {
		when(agent.chat("telegram-42", "Et ses comptes ?")).thenReturn("Elle a 2 comptes.");

		mockMvc.perform(get("/chat").param("query", "Et ses comptes ?").param("conversationId", "telegram-42"))
				.andExpect(status().isOk())
				.andExpect(content().string("Elle a 2 comptes."));
	}

	@Test
	void chatRequiresAQuery() throws Exception {
		mockMvc.perform(get("/chat")).andExpect(status().isBadRequest());
	}

}
