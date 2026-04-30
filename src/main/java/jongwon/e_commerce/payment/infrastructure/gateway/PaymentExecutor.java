package jongwon.e_commerce.payment.infrastructure.gateway;

import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;

public interface PaymentExecutor {
    boolean supports(PGType pgType);
    PayApproveOutcome executePayApprove(PayApproveAttempt request);
}
