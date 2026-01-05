package jongwon.e_commerce.product.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class InvalidProductPriceException extends DomainException {
    public InvalidProductPriceException() {
    }
    public InvalidProductPriceException(String message) {
        super(message);
    }
}
