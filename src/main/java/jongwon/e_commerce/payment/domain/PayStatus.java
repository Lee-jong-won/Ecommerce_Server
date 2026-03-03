package jongwon.e_commerce.payment.domain;

public enum PayStatus {
    PENDING,             // 결제 진행 중
    COMPLETE,           // 승인 완료
    FAILED,
    // ( 인증 실패 -> 프론트에서 FAIL URL을 호출 후, 백앤드에서 실패 처리 )
    // ( 승인 실패 -> 백앤드에서 결제승인 호출 후, 오류 응답을 받으면, 실패 처리)
    REFUND,           // 결제 취소
    TIME_OUT,           // 타임 아웃
}
