package jongwon.e_commerce.payment.gateway;

import jongwon.e_commerce.payment.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;

public interface PaymentClient {
    PayResult callPayApprovalApi(PayApproveAttempt request);
}
