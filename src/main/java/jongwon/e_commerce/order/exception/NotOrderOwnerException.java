package jongwon.e_commerce.order.exception;

public class NotOrderOwnerException extends RuntimeException {

    public NotOrderOwnerException(Long requestId, Long orderedId){
        super("인증된 사용자: " + orderedId + "주문 요청자: " + requestId);
    }

}
