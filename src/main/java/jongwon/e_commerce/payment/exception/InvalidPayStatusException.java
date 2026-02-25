package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class InvalidPayStatusException extends DomainException {

    public InvalidPayStatusException() {
    }
    public InvalidPayStatusException(String message) {
        super(message);
    }

}
