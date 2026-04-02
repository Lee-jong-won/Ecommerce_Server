package jongwon.e_commerce.order.exception;

public class NotOrderOwnerException extends RuntimeException {

    public NotOrderOwnerException(String message) {
        super(message);
    }


}
