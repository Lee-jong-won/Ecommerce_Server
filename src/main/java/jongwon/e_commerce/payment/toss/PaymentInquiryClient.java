package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;

public interface PaymentInquiryClient {
    TossPaymentApproveResponse callPayInquiryApi(String paymentKey);
}
