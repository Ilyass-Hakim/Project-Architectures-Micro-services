package org.emsi.EBank_Service.exceptions;

public class BankAccountNotFoundException extends RuntimeException {

    public BankAccountNotFoundException(String id) {
        super("Bank account %s not found".formatted(id));
    }
}
