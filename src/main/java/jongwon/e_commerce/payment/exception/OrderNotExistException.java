package jongwon.e_commerce.payment.exception;

public class OrderNotExistException extends RuntimeException {
    public OrderNotExistException() {
    }
    public OrderNotExistException(String message) {
        super(message);
    }
}
