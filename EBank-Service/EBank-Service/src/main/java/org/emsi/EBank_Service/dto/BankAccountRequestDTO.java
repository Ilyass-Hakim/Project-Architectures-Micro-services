package org.emsi.EBank_Service.dto;

import org.emsi.EBank_Service.enums.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountRequestDTO {

    @NotNull
    @PositiveOrZero
    private Double balance;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @NotNull
    private AccountType type;

    @NotNull
    private Long customerId;
}
