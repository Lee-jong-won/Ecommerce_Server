package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.dto.TossPaymentCancelResponse;

public interface TossPaymentClient {
    TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request,
                                                  String idempotencyKey);
    TossPaymentCancelResponse callPayCancelApi(String paymentKey,
                                               String cancelReason,
                                               String idempotencyKey);
}
