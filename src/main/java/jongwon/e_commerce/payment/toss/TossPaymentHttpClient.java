package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.dto.TossPaymentCancelResponse;

public interface TossPaymentHttpClient {
    TossPaymentApproveResponse callPayApprovalApi(TossPaymentApproveRequest request,
                                                  String idempotencyKey);
    TossPaymentCancelResponse callPayCancelApi(String paymentKey,
                                               String cancelReason,
                                               String idempotencyKey);
}
