package jongwon.e_commerce.payment.infrastructure.gateway;

import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;

public interface PaymentClient {
    PayResult callPayApprovalApi(PayApproveAttempt request);
}
