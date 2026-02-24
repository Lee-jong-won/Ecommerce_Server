package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.dto.TossPaymentCancelResponse;

public interface TossPaymentClient {
    TossPaymentApproveResponse callPayApprovalApi(TossPaymentApproveRequest request,
                                                  String idempotencyKey);
    TossPaymentCancelResponse callPayCancelApi(String paymentKey,
                                               String cancelReason,
                                               String idempotencyKey);
}
