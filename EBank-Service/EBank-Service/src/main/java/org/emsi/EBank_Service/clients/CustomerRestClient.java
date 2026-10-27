package org.emsi.EBank_Service.clients;

import java.util.List;

import org.emsi.EBank_Service.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client déclaratif OpenFeign : on décrit l'API distante avec les annotations
 * Spring MVC, et Spring génère l'implémentation qui fait les appels HTTP.
 *
 * - name : nom du service (servira à le retrouver dans Eureka plus tard)
 * - url  : adresse en dur tant qu'Eureka n'existe pas (vide => recherche par nom)
 */
@FeignClient(name = "customer-service", url = "${customer-service.url:}")
public interface CustomerRestClient {

    @GetMapping("/api/customers/{id}")
    Customer findCustomerById(@PathVariable("id") Long id);

    @GetMapping("/api/customers")
    List<Customer> allCustomers();
}
