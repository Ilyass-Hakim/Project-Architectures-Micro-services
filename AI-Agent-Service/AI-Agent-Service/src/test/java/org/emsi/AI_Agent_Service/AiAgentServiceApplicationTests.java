package org.emsi.AI_Agent_Service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.emsi.AI_Agent_Service.agents.EbankAiAgent;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Vérifie que l'agent se construit (ChatClient + outils + mémoire),
 * sans Eureka, sans vraie clé OpenAI et sans serveurs MCP démarrés.
 */
@SpringBootTest(properties = {
		"DISCOVERY_ENABLED=false",
		"spring.ai.openai.api-key=test-key",
		"spring.ai.mcp.client.enabled=false" })
class AiAgentServiceApplicationTests {

	@TestConfiguration
	static class NoMcpServers {
		// MCP Client désactivé : on fournit une liste d'outils vide à la place
		@Bean
		ToolCallbackProvider noMcpTools() {
			return ToolCallbackProvider.from(List.of());
		}
	}

	@Autowired
	private EbankAiAgent agent;

	@Test
	void agentIsCreated() {
		assertThat(agent).isNotNull();
	}

}
