package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class UnsupportedPayMethodException extends DomainException {
    public UnsupportedPayMethodException() {
    }
    public UnsupportedPayMethodException(String message) {
        super(message);
    }

}
