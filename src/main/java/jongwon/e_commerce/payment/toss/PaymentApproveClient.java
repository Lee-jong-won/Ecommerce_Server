package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;

public interface PaymentApproveClient {
    TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request,
                                                  String idempotencyKey);
}
