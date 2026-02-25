package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class PaymentNotFoundException extends DomainException {

    public PaymentNotFoundException() {
    }
    public PaymentNotFoundException(String message) {
        super(message);
    }

}
