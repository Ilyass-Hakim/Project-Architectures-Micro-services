package org.emsi.Customer_Service.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer %d not found".formatted(id));
    }
}
