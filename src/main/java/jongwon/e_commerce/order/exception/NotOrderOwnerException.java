package jongwon.e_commerce.order.exception;

public class NotOrderOwnerException extends RuntimeException {

    public NotOrderOwnerException(String message) {
        super(message);
    }

    public NotOrderOwnerException(Long requestId, Long orderedId){
        super("주문의 소유자: " + orderedId + "결제 요청자: " + requestId);
    }

}
