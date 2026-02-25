package jongwon.e_commerce.product.exception;

import jongwon.e_commerce.common.exception.DomainException;

public class NotEnoughStockException extends DomainException {
    public NotEnoughStockException() {
    }
    public NotEnoughStockException(String message) {
        super(message);
    }
}
