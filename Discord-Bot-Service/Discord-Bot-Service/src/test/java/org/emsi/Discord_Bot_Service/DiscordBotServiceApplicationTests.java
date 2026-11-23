package org.emsi.Discord_Bot_Service;

import static org.assertj.core.api.Assertions.assertThat;

import org.emsi.Discord_Bot_Service.agent.AiAgentClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Le service démarre sans token Discord : le client de l'agent est créé, le bot non.
 */
@SpringBootTest
class DiscordBotServiceApplicationTests {

	@Autowired
	private AiAgentClient agentClient;

	@Test
	void contextLoads() {
		assertThat(agentClient).isNotNull();
	}

}
