package org.emsi.EBank_Service.entities;

import java.time.LocalDate;

import org.emsi.EBank_Service.enums.AccountType;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "fullAccount", types = BankAccount.class)
public interface BankAccountProjection {
    String getId();
    LocalDate getCreatedAt();
    Double getBalance();
    String getCurrency();
    AccountType getType();
    Long getCustomerId();
}
