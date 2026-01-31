package jongwon.e_commerce.order.domain;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,
    SHIPPED,
    FAILED,
    EXPIRED,
    CANCELLED
}
