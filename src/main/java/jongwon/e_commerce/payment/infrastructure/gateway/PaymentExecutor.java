package jongwon.e_commerce.payment.infrastructure.gateway;

import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;

public interface PaymentExecutor {
    boolean supports(PGType pgType);
    PayResult executePayApprove(PayApproveAttempt attempt);
}
