package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;

public interface PayOutcomeHandler {
    boolean supports(PayApproveOutcomeType type);
    PayApproveOutcomeResponse handle(Pay pay, PayApproveOutcome outcome);
}
