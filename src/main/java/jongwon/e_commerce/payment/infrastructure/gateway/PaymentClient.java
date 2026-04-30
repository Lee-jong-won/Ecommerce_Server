package jongwon.e_commerce.payment.infrastructure.gateway;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;

public interface PaymentClient {
    PayApproveOutcome callPayApprovalApi(PayApproveAttempt request);
}
