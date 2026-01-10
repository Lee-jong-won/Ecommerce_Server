package jongwon.e_commerce.product.domain;

public enum ProductStatus {
    READY,      // 판매대기
    SELLING,    // 판매중
    STOPPED,    // 판매중지 (재고 소진 시 자동 전이 대상)
    ENDED       // 판매종료 (STOPPED에서만 가능)
}
