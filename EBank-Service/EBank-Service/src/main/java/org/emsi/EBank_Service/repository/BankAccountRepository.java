package org.emsi.EBank_Service.repository;

import java.util.List;

import org.emsi.EBank_Service.entities.BankAccount;
import org.emsi.EBank_Service.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "bankAccounts", collectionResourceRel = "bankAccounts")
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

    @RestResource(path = "byType")
    List<BankAccount> findByType(@Param("type") AccountType type);

    @RestResource(path = "byCustomer")
    List<BankAccount> findByCustomerId(@Param("customerId") Long customerId);
}
