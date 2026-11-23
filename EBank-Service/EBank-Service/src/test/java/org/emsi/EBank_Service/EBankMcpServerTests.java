package org.emsi.EBank_Service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.emsi.EBank_Service.clients.CustomerRestClient;
import org.emsi.EBank_Service.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * Teste le serveur MCP d'EBank avec un vrai MCP Client (SDK Java MCP).
 * Customer-Service est remplacé par un mock du client Feign.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "DISCOVERY_ENABLED=false",
        // base H2 dédiée : ce contexte ne partage pas ses données avec les autres classes de test
        "spring.datasource.url=jdbc:h2:mem:ebank-mcp-test" })
class EBankMcpServerTests {

    @Value("${local.server.port}")
    private int port;

    @MockitoBean
    private CustomerRestClient customerRestClient;

    @BeforeEach
    void mockCustomerService() {
        when(customerRestClient.findCustomerById(anyLong()))
                .thenAnswer(call -> new Customer(call.getArgument(0), "Imane", "imane@gmail.com"));
    }

    @Test
    void mcpClientDiscoversAndCallsBankAccountTools() {
        var transport = HttpClientStreamableHttpTransport.builder("http://localhost:" + port).build();
        try (McpSyncClient client = McpClient.sync(transport).requestTimeout(Duration.ofSeconds(10)).build()) {
            McpSchema.InitializeResult init = client.initialize();
            assertThat(init.serverInfo().name()).isEqualTo("ebank-mcp-server");

            List<String> tools = client.listTools().tools().stream().map(McpSchema.Tool::name).toList();
            assertThat(tools).contains("getAllBankAccounts", "getBankAccountById",
                    "getBankAccountsByCustomer", "createBankAccount");

            McpSchema.CallToolResult accounts = client.callTool(
                    new McpSchema.CallToolRequest("getBankAccountsByCustomer", Map.of("customerId", 2)));
            assertThat(accounts.isError()).isNotEqualTo(Boolean.TRUE);
            assertThat(text(accounts)).contains("SAVING_ACCOUNT", "Imane");

            McpSchema.CallToolResult created = client.callTool(new McpSchema.CallToolRequest("createBankAccount",
                    Map.of("balance", 2500, "currency", "EUR", "type", "CURRENT_ACCOUNT", "customerId", 2)));
            assertThat(created.isError()).isNotEqualTo(Boolean.TRUE);
            assertThat(text(created)).contains("EUR", "CURRENT_ACCOUNT");
        }
    }

    private static String text(McpSchema.CallToolResult result) {
        return ((McpSchema.TextContent) result.content().get(0)).text();
    }
}
