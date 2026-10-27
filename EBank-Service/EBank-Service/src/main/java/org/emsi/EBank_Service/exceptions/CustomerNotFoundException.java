package org.emsi.EBank_Service.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer %d does not exist in Customer-Service".formatted(id));
    }
}
