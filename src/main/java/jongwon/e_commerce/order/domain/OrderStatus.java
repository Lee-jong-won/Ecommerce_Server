package jongwon.e_commerce.order.domain;

public enum OrderStatus {
    ORDERED,
    PAYMENT_PENDING,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    // 결제 승인 및 인증 실패
    // ( 인증 실패 -> 프론트에서 FAIL URL을 호출 후, 백앤드에서 실패 처리 )
    // ( 승인 실패 -> 백앤드에서 결제승인 호출 후, 오류 응답을 받으면, 실패 처리)
    PAYMENT_TIMEOUT,
    PAYMENT_EXPIRED, // 결제 승인이 이루어 지지 않아 만료된 상태
    CANCELLED
}
