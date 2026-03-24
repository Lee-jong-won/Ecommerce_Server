package jongwon.e_commerce.product.exception;

public class InvalidProductStatusException extends RuntimeException {

    public InvalidProductStatusException() {
    }
    public InvalidProductStatusException(String message) {
        super(message);
    }

}
