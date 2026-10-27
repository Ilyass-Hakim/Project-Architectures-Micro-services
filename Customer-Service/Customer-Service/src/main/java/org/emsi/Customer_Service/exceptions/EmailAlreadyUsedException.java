package org.emsi.Customer_Service.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("Email %s is already used by another customer".formatted(email));
    }
}
