package org.emsi.AI_Agent_Service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Partie 2 : Agent IA basé sur Spring AI.
 * Il utilise OpenAI comme LLM et se connecte en MCP Client aux serveurs MCP
 * de Customer-Service (8081/mcp) et EBank-Service (8082/mcp) pour répondre
 * aux questions sur les clients et les comptes.
 * Démarrer Customer-Service et EBank-Service AVANT l'agent.
 */
@SpringBootApplication
public class AiAgentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiAgentServiceApplication.class, args);
	}

}
