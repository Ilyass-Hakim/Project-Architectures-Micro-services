package org.emsi.EBank_Service.mcp;

import java.util.List;

import org.emsi.EBank_Service.dto.BankAccountRequestDTO;
import org.emsi.EBank_Service.dto.BankAccountResponseDTO;
import org.emsi.EBank_Service.enums.AccountType;
import org.emsi.EBank_Service.service.BankAccountService;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpTool.McpAnnotations;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

/**
 * Partie 2 : expose les fonctionnalités d'EBank-Service comme outils MCP,
 * publiés sur http://localhost:8082/mcp (Streamable-HTTP).
 * Chaque compte renvoyé contient son client, récupéré via OpenFeign
 * (et "Not available" si Customer-Service est en panne : Circuit Breaker).
 */
@Component
public class BankAccountMcpTools {

    private final BankAccountService bankAccountService;

    public BankAccountMcpTools(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @McpTool(name = "getAllBankAccounts",
            description = "List all the bank accounts with their balance, currency, type and customer",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public List<BankAccountResponseDTO> getAllBankAccounts() {
        return bankAccountService.findAll();
    }

    @McpTool(name = "getBankAccountById",
            description = "Get one bank account by its id (UUID)",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public BankAccountResponseDTO getBankAccountById(
            @McpToolParam(description = "Bank account id (UUID)") String id) {
        return bankAccountService.findById(id);
    }

    @McpTool(name = "getBankAccountsByCustomer",
            description = "List the bank accounts of one customer",
            annotations = @McpAnnotations(readOnlyHint = true, destructiveHint = false))
    public List<BankAccountResponseDTO> getBankAccountsByCustomer(
            @McpToolParam(description = "Customer id") Long customerId) {
        return bankAccountService.findByCustomerId(customerId);
    }

    @McpTool(name = "createBankAccount",
            description = "Open a new bank account for an existing customer",
            annotations = @McpAnnotations(readOnlyHint = false, destructiveHint = false))
    public BankAccountResponseDTO createBankAccount(
            @McpToolParam(description = "Initial balance (zero or positive)") Double balance,
            @McpToolParam(description = "Currency, 3 letters ISO code, e.g. MAD, EUR, USD") String currency,
            @McpToolParam(description = "Account type: CURRENT_ACCOUNT or SAVING_ACCOUNT") AccountType type,
            @McpToolParam(description = "Id of the customer who owns the account") Long customerId) {
        return bankAccountService.create(BankAccountRequestDTO.builder()
                .balance(balance)
                .currency(currency)
                .type(type)
                .customerId(customerId)
                .build());
    }
}
