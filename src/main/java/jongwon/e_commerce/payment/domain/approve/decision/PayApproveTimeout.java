package jongwon.e_commerce.payment.domain.approve.decision;

import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.dto.body.PayFailBody;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PayApproveTimeout extends PayApproveOutcome {
    public PayApproveTimeout(){
        super(PayApproveOutcomeType.TIMEOUT);
    }

    public PayApproveOutcomeResponse toPayFailureResponse(String code,
                                                          String message){
        return PayApproveOutcomeResponse.
                builder().
                httpStatus(HttpStatus.GATEWAY_TIMEOUT).payResponseBody(
                        PayFailBody.builder().
                        message(message).
                        code(code).
                        build()).
                build();
    }

}
