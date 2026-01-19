package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.InfrastructureException;

public class PGTooManyRequestException extends InfrastructureException {

    public PGTooManyRequestException() {
    }
    public PGTooManyRequestException(String message) {
        super(message);
    }

}
