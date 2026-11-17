package org.emsi.EBank_Service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copie "locale" d'un client de Customer-Service.
 * Ce n'est PAS une entité JPA : EBank ne stocke pas les clients,
 * il les demande à Customer-Service via OpenFeign.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    public static final String NOT_AVAILABLE = "Not available";

    private Long id;
    private String name;
    private String email;

    /** Client "de secours" renvoyé par le fallback quand Customer-Service est injoignable. */
    public static Customer unavailable(Long id) {
        return new Customer(id, NOT_AVAILABLE, NOT_AVAILABLE);
    }

    @JsonIgnore
    public boolean isUnavailable() {
        return NOT_AVAILABLE.equals(name);
    }
}
