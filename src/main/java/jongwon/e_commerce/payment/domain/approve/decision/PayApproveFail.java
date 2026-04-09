package jongwon.e_commerce.payment.domain.approve.decision;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PayApproveFail extends PayApproveOutcome {

    private final String errorCode;
    private final String message;

    public PayApproveFail(String errorCode, String message, HttpStatus httpStatus){
        super(PayApproveOutcomeType.FAIL, httpStatus);
        this.errorCode = errorCode;
        this.message = message;
    }
}
