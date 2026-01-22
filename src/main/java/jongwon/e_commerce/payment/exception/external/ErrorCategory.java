package jongwon.e_commerce.payment.exception.external;

public enum ErrorCategory {
    USER_FAULT,        // 사용자 입력 / 비즈니스 오류
    REJECT,            // 카드사·은행·FDS 거절
    RETRYABLE,         // 재시도 가능 (일시 장애)
    NOT_FOUND,         // 리소스 없음
    SECURITY,          // 인증 / 보안
    ALREADY_PROCESSED, // 멱등 처리
    UNKNOWN
}
