package org.emsi.EBank_Service.exceptions;

public class CustomerServiceUnavailableException extends RuntimeException {

    public CustomerServiceUnavailableException() {
        super("Customer-Service is unavailable, please retry later");
    }
}
