package jongwon.e_commerce.payment.gateway;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;

public interface PaymentExecutor {
    boolean supports(String providerName);
    PayApproveOutcome executePayApprove(PayApproveAttempt request);
}
