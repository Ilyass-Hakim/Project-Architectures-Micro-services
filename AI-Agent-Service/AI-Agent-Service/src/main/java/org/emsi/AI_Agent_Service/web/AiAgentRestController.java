package org.emsi.AI_Agent_Service.web;

import org.emsi.AI_Agent_Service.agents.EbankAiAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Client REST du chatbot :
 *   GET http://localhost:8090/chat?query=Liste des clients
 *   GET http://localhost:8888/chat?query=...   (via la Gateway)
 * conversationId (optionnel) : sépare la mémoire de plusieurs conversations.
 */
@RestController
public class AiAgentRestController {

    private final EbankAiAgent agent;

    public AiAgentRestController(EbankAiAgent agent) {
        this.agent = agent;
    }

    // charset=UTF-8 : sinon les accents de la réponse sont mal affichés
    @GetMapping(value = "/chat", produces = "text/plain;charset=UTF-8")
    public String chat(@RequestParam String query,
                       @RequestParam(defaultValue = EbankAiAgent.DEFAULT_CONVERSATION_ID) String conversationId) {
        return agent.chat(conversationId, query);
    }
}
