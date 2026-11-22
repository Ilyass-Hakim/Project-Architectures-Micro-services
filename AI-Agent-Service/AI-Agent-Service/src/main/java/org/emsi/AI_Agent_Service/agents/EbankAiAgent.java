package org.emsi.AI_Agent_Service.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

/**
 * L'agent IA de la banque.
 * - tools : les outils MCP de Customer-Service et EBank-Service (fournis par le MCP Client)
 * - chatMemory : la mémoire de conversation (Spring Boot 4 : on passe un CONVERSATION_ID)
 */
@Service
public class EbankAiAgent {

    public static final String DEFAULT_CONVERSATION_ID = "C1";

    private static final String SYSTEM_PROMPT = """
            Tu es l'assistant virtuel de la banque EBank.
            Tu aides les conseillers à consulter les clients et leurs comptes bancaires.
            Utilise toujours les outils à ta disposition pour obtenir des données réelles :
            n'invente jamais un client, un compte ou un solde.
            Si un outil renvoie une erreur ou "Not available", explique-le simplement.
            Avant de créer un client ou un compte, vérifie que tu as toutes les informations nécessaires.
            Réponds dans la langue de l'utilisateur, de façon claire et concise.
            """;

    private final ChatClient chatClient;

    public EbankAiAgent(ChatClient.Builder chatClient, ToolCallbackProvider tools, ChatMemory chatMemory) {
        this.chatClient = chatClient
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(tools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String chat(String query) {
        return chat(DEFAULT_CONVERSATION_ID, query);
    }

    /**
     * @param conversationId identifiant de la conversation : chaque conversation a sa propre mémoire
     *                       (plus tard : un id par utilisateur Telegram / Discord)
     */
    public String chat(String conversationId, String query) {
        return chatClient.prompt(query)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call().content();
    }
}
