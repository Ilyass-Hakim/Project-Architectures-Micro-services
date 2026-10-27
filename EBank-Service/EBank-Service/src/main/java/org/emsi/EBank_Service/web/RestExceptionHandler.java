package org.emsi.EBank_Service.web;

import org.emsi.EBank_Service.exceptions.BankAccountNotFoundException;
import org.emsi.EBank_Service.exceptions.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = BankAccountRestController.class)
public class RestExceptionHandler {

    @ExceptionHandler(BankAccountNotFoundException.class)
    public ProblemDetail handleNotFound(BankAccountNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleCustomerNotFound(CustomerNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}
