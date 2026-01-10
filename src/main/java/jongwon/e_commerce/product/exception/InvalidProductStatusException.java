package jongwon.e_commerce.product.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class InvalidProductStatusException extends DomainException {

    public InvalidProductStatusException() {
    }
    public InvalidProductStatusException(String message) {
        super(message);
    }

}
