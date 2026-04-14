package jongwon.e_commerce.payment.domain.approve.result.fail;

import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;

public interface PayApproveFail extends PayApproveOutcome {
    // PayFailureHandler에 의해 처리됨
    PayErrorCode errorCode();
}
