package jongwon.e_commerce.payment.exception;

public class InvalidPayStatusException extends RuntimeException {

    public InvalidPayStatusException() {
    }
    public InvalidPayStatusException(String message) {
        super(message);
    }

}
