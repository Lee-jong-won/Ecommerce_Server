package jongwon.e_commerce.payment.exception;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException() {
    }
    public InvalidAmountException(String message) {
        super(message);
    }


}
