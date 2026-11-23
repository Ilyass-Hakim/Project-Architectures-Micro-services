package org.emsi.Discord_Bot_Service.agent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/**
 * Vérifie l'appel REST vers l'agent avec un serveur HTTP simulé.
 */
class AiAgentClientTests {

    @Test
    void sendsTheEncodedQuestionWithItsConversationId() {
        RestClient.Builder builder = AiAgentClient.configure(RestClient.builder(), "http://agent");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        AiAgentClient client = new AiAgentClient(builder.build());

        server.expect(requestTo("http://agent/chat?query=Comptes%20de%20Imane%20%26%20total%20%3F&conversationId=discord-42"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("Imane a 2 comptes, solde élevé.",
                        new MediaType("text", "plain", StandardCharsets.UTF_8)));

        assertThat(client.chat("discord-42", "Comptes de Imane & total ?"))
                .isEqualTo("Imane a 2 comptes, solde élevé.");
        server.verify();
    }
}
