package jongwon.e_commerce.order.exception;

public class InvalidDeliveryStateException extends RuntimeException {

    public InvalidDeliveryStateException() {
    }
    public InvalidDeliveryStateException(String message) {
        super(message);
    }

}
