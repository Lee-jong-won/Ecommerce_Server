package jongwon.e_commerce.payment.domain;

public enum PayStatus {
    PENDING,    // 결제 진행 중
    COMPLETE,   // 승인 완료
    BUSINESS_FAILED,     // 잔액 부족, 카드 오류 - 다른 paymentKey로 재시도
    SERVER_FAILED, // 게이트 웨이 / 네트워크 오류 - 같은 paymentKey로 재시도
    REFUND,     // 결제 취소
    UNKNOWN,    // 처리 여부 불명확 (PG 응답 확인 불가)
}
