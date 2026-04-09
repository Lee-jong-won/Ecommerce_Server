package jongwon.e_commerce.payment.domain.approve.decision;

import jongwon.e_commerce.payment.application.approve.external.PayErrorCode;
import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.dto.body.PayFailBody;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PayApproveFail extends PayApproveOutcome {

    private final PayErrorCode payErrorCode;
    private final String message;

    public PayApproveFail(PayErrorCode errorCode, String message){
        super(PayApproveOutcomeType.FAIL);
        this.payErrorCode = errorCode;
        this.message = message;
    }

    public PayApproveOutcomeResponse toPayApproveOutcomeResponse(HttpStatus httpStatus){
        return PayApproveOutcomeResponse.
                builder().
                httpStatus(httpStatus).payResponseBody
                        (PayFailBody.
                        builder().
                        code(payErrorCode.toString()).
                        message(message).
                        build()).
                build();
    }
}
