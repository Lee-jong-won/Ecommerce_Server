package jongwon.e_commerce.order.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class InvalidDeliveryStateException extends DomainException {

    public InvalidDeliveryStateException() {
    }
    public InvalidDeliveryStateException(String message) {
        super(message);
    }

}
