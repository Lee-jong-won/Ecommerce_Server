package jongwon.e_commerce.payment.domain;

public enum PayStatus {
    PENDING,           // 승인 요청 시작
    SUCCESS,           // 승인 완료
    FAILED,            // 명확한 승인 실패
    SYNC_TIMEOUT,      // 승인 결과 미수신 (동기화 실패)
    EXPIRED,           // PG 정책상 만료 (30분 초과)
    WAITING_DEPOSIT,   // 가상계좌 입금 대기
    CANCELED           // 결제 취소
}
