package jongwon.e_commerce.payment.domain.approve.decision;

import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.dto.body.PaySuccessBody;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PayApproveSuccess extends PayApproveOutcome {
    private final PayResult payResult;
    public PayApproveSuccess(PayResult payResult) {
        super(PayApproveOutcomeType.SUCCESS);
        this.payResult = payResult;
    }

    public PayApproveOutcomeResponse toPayApproveOutcomeResponse(HttpStatus httpStatus){


        return PayApproveOutcomeResponse.builder().payResponseBody(
                PaySuccessBody.builder().
                        payAmount().
                        payMethod(payMethod).
                        build()).httpStatus(httpStatus).build();
    }

}
