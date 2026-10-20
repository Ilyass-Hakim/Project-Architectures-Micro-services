package org.emsi.EBank_Service.entities;

import java.time.LocalDate;

import org.emsi.EBank_Service.enums.AccountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(updatable = false)
    private LocalDate createdAt;

    @NotNull
    @PositiveOrZero
    private Double balance;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType type;

    // Customers live in Customer-Service, so only their id is stored here
    @NotNull
    private Long customerId;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDate.now();
    }
}
