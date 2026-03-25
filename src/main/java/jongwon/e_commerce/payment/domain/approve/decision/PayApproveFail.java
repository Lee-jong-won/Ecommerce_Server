package jongwon.e_commerce.payment.domain.approve.decision;

import lombok.Getter;

@Getter
public class PayApproveFail extends PayApproveOutcome {

    private final String errorCode;
    private final String message;

    public PayApproveFail(String errorCode, String message){
        super(PayApproveOutcomeType.FAIL);
        this.errorCode = errorCode;
        this.message = message;
    }
}
