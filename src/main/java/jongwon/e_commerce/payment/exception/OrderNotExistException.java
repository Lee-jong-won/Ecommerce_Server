package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class OrderNotExistException extends DomainException {
    public OrderNotExistException() {
    }
    public OrderNotExistException(String message) {
        super(message);
    }
}
