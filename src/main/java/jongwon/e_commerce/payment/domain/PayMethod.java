package jongwon.e_commerce.payment.domain;

public enum PayMethod {
    CARD,               // 카드
    TRANSFER,           // 계좌이체
    VIRTUAL_ACCOUNT,    // 가상계좌
    MOBILE,             // 휴대폰 결제
    EASY_PAY,           // 간편결제
    GIFT_CERTIFICATE    // 상품권 (문화/도서/게임)
}
