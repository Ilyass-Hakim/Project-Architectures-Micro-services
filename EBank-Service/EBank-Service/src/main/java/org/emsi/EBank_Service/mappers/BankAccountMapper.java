package org.emsi.EBank_Service.mappers;

import org.emsi.EBank_Service.dto.BankAccountRequestDTO;
import org.emsi.EBank_Service.dto.BankAccountResponseDTO;
import org.emsi.EBank_Service.entities.BankAccount;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    public BankAccountResponseDTO toResponse(BankAccount account) {
        return BankAccountResponseDTO.builder()
                .id(account.getId())
                .createdAt(account.getCreatedAt())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .type(account.getType())
                .customerId(account.getCustomerId())
                .build();
    }

    public BankAccount toEntity(BankAccountRequestDTO request) {
        BankAccount account = new BankAccount();
        updateEntity(account, request);
        return account;
    }

    public void updateEntity(BankAccount account, BankAccountRequestDTO request) {
        account.setBalance(request.getBalance());
        account.setCurrency(request.getCurrency().toUpperCase());
        account.setType(request.getType());
        account.setCustomerId(request.getCustomerId());
    }
}
