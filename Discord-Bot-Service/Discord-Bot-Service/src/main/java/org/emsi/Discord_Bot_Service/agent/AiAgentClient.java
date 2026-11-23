package org.emsi.Discord_Bot_Service.agent;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * Client REST de l'agent IA : GET {agent-url}/chat?query=...&conversationId=...
 * Par défaut agent-url = la Gateway (http://localhost:8888), qui route /chat vers ai-agent-service.
 */
@Component
public class AiAgentClient {

    private final RestClient restClient;

    @Autowired
    public AiAgentClient(@Value("${discord.agent-url}") String agentUrl) {
        this(configure(RestClient.builder(), agentUrl).requestFactory(requestFactory()).build());
    }

    // utilisé par les tests (serveur HTTP simulé)
    AiAgentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    static RestClient.Builder configure(RestClient.Builder builder, String agentUrl) {
        DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory(agentUrl);
        // encode entièrement les questions (espaces, &, ?, +, accents...) dans l'URL
        uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        return builder.uriBuilderFactory(uriFactory);
    }

    private static JdkClientHttpRequestFactory requestFactory() {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        // l'agent appelle le LLM et les outils MCP : la réponse peut prendre du temps
        factory.setReadTimeout(Duration.ofSeconds(120));
        return factory;
    }

    public String chat(String conversationId, String query) {
        return restClient.get()
                .uri("/chat?query={query}&conversationId={conversationId}", query, conversationId)
                .retrieve()
                .body(String.class);
    }
}
