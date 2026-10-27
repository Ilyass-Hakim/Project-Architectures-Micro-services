package org.emsi.EBank_Service.dto;

import java.time.LocalDate;

import org.emsi.EBank_Service.enums.AccountType;
import org.emsi.EBank_Service.model.Customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountResponseDTO {
    private String id;
    private LocalDate createdAt;
    private Double balance;
    private String currency;
    private AccountType type;
    private Long customerId;
    // Rempli grâce à l'appel OpenFeign vers Customer-Service
    private Customer customer;
}
