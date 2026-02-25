package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class InvalidAmountException extends DomainException {

    public InvalidAmountException() {
    }
    public InvalidAmountException(String message) {
        super(message);
    }


}
