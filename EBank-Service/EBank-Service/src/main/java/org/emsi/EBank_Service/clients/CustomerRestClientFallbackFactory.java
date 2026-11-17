package org.emsi.EBank_Service.clients;

import java.util.List;

import org.emsi.EBank_Service.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import feign.FeignException;

/**
 * Plan B quand un appel vers Customer-Service échoue
 * (service arrêté, trop lent, ou circuit ouvert).
 *
 * On utilise une FallbackFactory (et pas un simple fallback) pour connaître
 * la cause de l'échec : un 404 n'est pas une panne, c'est une vraie réponse
 * ("ce client n'existe pas"), donc on la laisse passer au lieu de la masquer.
 */
@Component
public class CustomerRestClientFallbackFactory implements FallbackFactory<CustomerRestClient> {

    private static final Logger log = LoggerFactory.getLogger(CustomerRestClientFallbackFactory.class);

    @Override
    public CustomerRestClient create(Throwable cause) {
        FeignException.NotFound notFound = findNotFound(cause);

        return new CustomerRestClient() {
            @Override
            public Customer findCustomerById(Long id) {
                if (notFound != null) throw notFound;
                log.warn("Customer-Service unavailable, fallback for customer {} ({})", id, cause.toString());
                return Customer.unavailable(id);
            }

            @Override
            public List<Customer> allCustomers() {
                log.warn("Customer-Service unavailable, fallback for all customers ({})", cause.toString());
                return List.of();
            }
        };
    }

    // La cause peut être enveloppée dans une autre exception : on remonte la chaîne
    private static FeignException.NotFound findNotFound(Throwable t) {
        while (t != null) {
            if (t instanceof FeignException.NotFound nf) return nf;
            t = t.getCause();
        }
        return null;
    }
}
