package jongwon.e_commerce.product.exception;

public class InvalidProductPriceException extends RuntimeException {
    public InvalidProductPriceException() {
    }
    public InvalidProductPriceException(String message) {
        super(message);
    }
}
