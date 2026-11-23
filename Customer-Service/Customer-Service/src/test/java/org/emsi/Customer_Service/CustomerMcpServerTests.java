package org.emsi.Customer_Service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * Teste le serveur MCP avec un vrai MCP Client (SDK Java MCP), comme le fera l'agent IA :
 * connexion à /mcp, découverte des outils, puis appels.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "DISCOVERY_ENABLED=false",
        // base H2 dédiée : ce contexte ne partage pas ses données avec les autres classes de test
        "spring.datasource.url=jdbc:h2:mem:customers-mcp-test" })
class CustomerMcpServerTests {

    @Value("${local.server.port}")
    private int port;

    @Test
    void mcpClientDiscoversAndCallsCustomerTools() {
        var transport = HttpClientStreamableHttpTransport.builder("http://localhost:" + port).build();
        try (McpSyncClient client = McpClient.sync(transport).requestTimeout(Duration.ofSeconds(10)).build()) {
            McpSchema.InitializeResult init = client.initialize();
            assertThat(init.serverInfo().name()).isEqualTo("customer-mcp-server");

            List<String> tools = client.listTools().tools().stream().map(McpSchema.Tool::name).toList();
            assertThat(tools).contains("getAllCustomers", "getCustomerById", "searchCustomers", "createCustomer");

            McpSchema.CallToolResult byId = client.callTool(
                    new McpSchema.CallToolRequest("getCustomerById", Map.of("id", 1)));
            assertThat(byId.isError()).isNotEqualTo(Boolean.TRUE);
            assertThat(text(byId)).contains("Mohamed");

            McpSchema.CallToolResult created = client.callTool(new McpSchema.CallToolRequest("createCustomer",
                    Map.of("name", "Sara", "email", "sara.mcp@gmail.com")));
            assertThat(created.isError()).isNotEqualTo(Boolean.TRUE);
            assertThat(text(created)).contains("sara.mcp@gmail.com");

            McpSchema.CallToolResult unknown = client.callTool(
                    new McpSchema.CallToolRequest("getCustomerById", Map.of("id", 9999)));
            assertThat(unknown.isError()).isTrue();
        }
    }

    private static String text(McpSchema.CallToolResult result) {
        return ((McpSchema.TextContent) result.content().get(0)).text();
    }
}
