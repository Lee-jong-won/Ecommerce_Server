package jongwon.e_commerce.payment.controller.dto;

import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public abstract class PayApproveOutcomeResponse {

    private final PayStatus payStatus;
    private final HttpStatus httpStatus;

    protected PayApproveOutcomeResponse(PayStatus payStatus, HttpStatus httpStatus){
        this.payStatus = payStatus;
        this.httpStatus = httpStatus;
    }

}
