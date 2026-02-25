package jongwon.e_commerce.order.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class InvalidOrderStateException extends DomainException {
    public InvalidOrderStateException() {
    }
    public InvalidOrderStateException(String message) {
        super(message);
    }
}
