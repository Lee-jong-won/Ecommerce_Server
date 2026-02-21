package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.payment.domain.Pay;

public interface PaymentApprovalPreprocessor {
    Pay preparePaymentApproval(String paymentId, String orderId, long amount);
}
