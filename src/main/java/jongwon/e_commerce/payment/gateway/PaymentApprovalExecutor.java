package jongwon.e_commerce.payment.gateway;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.gateway.toss.dto.PayApproveAttempt;

public interface PaymentApprovalExecutor {
    boolean supports(String providerName);
    PayApproveOutcome executePayApprove(PayApproveAttempt request);
}
