package jongwon.e_commerce.order.exception;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException() {
    }
    public InvalidOrderStateException(String message) {
        super(message);
    }
}
