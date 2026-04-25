package jongwon.e_commerce.payment.gateway.toss.dto;

import jongwon.e_commerce.payment.domain.detail.PaymentDetail;

public interface TossPaymentDetail {
    PaymentDetail toPaymentDetail();
}
