package org.emsi.Customer_Service.mcp;

import java.util.List;

import org.emsi.Customer_Service.dto.CustomerRequestDTO;
import org.emsi.Customer_Service.dto.CustomerResponseDTO;
import org.emsi.Customer_Service.service.CustomerService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpTool.McpAnnotations;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

/**
 * Partie 2 : expose les fonctionnalités de Customer-Service comme outils MCP.
 * Spring AI détecte les méthodes @McpTool au démarrage et les publie sur
 * http://localhost:8081/mcp (Streamable-HTTP). Un agent IA (MCP Client) peut
 * alors les découvrir et décider lui-même de les appeler.
 * Les outils réutilisent la couche service, comme le RestController.
 */
@Component
public class CustomerMcpTools {

    private final CustomerService customerService;

    public CustomerMcpTools(CustomerService customerService) {
        this.customerService = customerService;
    }

    @McpTool(name = "getAllCustomers",
            description = "List all the customers of the bank (id, name, email)",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerService.findAll();
    }

    @McpTool(name = "getCustomerById",
            description = "Get one customer of the bank by its id",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public CustomerResponseDTO getCustomerById(
            @McpToolParam(description = "Customer id") Long id) {
        return customerService.findById(id);
    }

    @McpTool(name = "searchCustomers",
            description = "Search the customers whose name contains a keyword (case insensitive)",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public List<CustomerResponseDTO> searchCustomers(
            @McpToolParam(description = "Keyword to look for in the customer name") String keyword) {
        return customerService.searchByName(keyword);
    }

    @McpTool(name = "createCustomer",
            description = "Create a new customer of the bank. The email must be unique.",
            annotations = @McpAnnotations(readOnlyHint = false, destructiveHint = false))
    public CustomerResponseDTO createCustomer(
            @McpToolParam(description = "Full name of the customer") String name,
            @McpToolParam(description = "Email address of the customer") String email) {
        return customerService.create(CustomerRequestDTO.builder().name(name).email(email).build());
    }
}
