package jongwon.e_commerce.payment.domain.approve.outcome.fail;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;

public interface PayApproveFail extends PayApproveOutcome {
    // PayFailureHandler에 의해 처리됨
    PayErrorCode errorCode();
}
