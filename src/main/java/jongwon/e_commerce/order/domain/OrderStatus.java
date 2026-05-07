package jongwon.e_commerce.order.domain;

public enum OrderStatus {
    ORDERED, // 처음 주문 생성 시 상태
    PAYMENT_PENDING, // 결제 전처리 완료
    FAIL, // 결제 실패 상태
    PAID, // 결제 성공 상태
    CANCEL // 결제 성공 후, 환불된 상태
}
