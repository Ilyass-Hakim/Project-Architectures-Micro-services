package org.emsi.Customer_Service.repository;

import java.util.List;
import java.util.Optional;

import org.emsi.Customer_Service.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(path = "customers", collectionResourceRel = "customers")
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @RestResource(path = "byName")
    List<Customer> findByNameContainsIgnoreCase(@Param("kw") String keyword);

    @RestResource(path = "byEmail")
    Optional<Customer> findByEmail(@Param("email") String email);
}
