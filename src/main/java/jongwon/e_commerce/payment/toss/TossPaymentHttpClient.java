package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;

public interface TossPaymentHttpClient {
    TossPaymentApproveResponse callPayApprovalApi(TossPaymentApproveRequest request,
                                                  String idempotencyKey);
    void callPayCancelApi(String paymentKey,
                          String cancelReason,
                          String idempotencyKey);
}
