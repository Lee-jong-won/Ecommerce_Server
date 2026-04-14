package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;

public interface PayOutcomeHandler {
    boolean supports(PayApproveOutcome outcome);
    void handle(Pay pay, PayApproveOutcome outcome);
}
