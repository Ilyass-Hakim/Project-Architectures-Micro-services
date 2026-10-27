package org.emsi.EBank_Service.model;

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
    private Long id;
    private String name;
    private String email;
}
