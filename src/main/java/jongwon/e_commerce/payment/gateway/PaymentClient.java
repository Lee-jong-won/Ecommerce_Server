package jongwon.e_commerce.payment.gateway;

import jongwon.e_commerce.payment.domain.approve.outcome.success.PayResult;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;

public interface PaymentClient {
    PayResult callPayApprovalApi(PayApproveAttempt request);
}
