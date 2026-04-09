package jongwon.e_commerce.payment.controller.dto;

import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PayFailureResponse extends PayApproveOutcomeResponse {
    private final String code;
    private final String message;
    public PayFailureResponse(HttpStatus httpStatus,
                              PayStatus payStatus,
                              String code,
                              String message){
        super(payStatus, httpStatus);
        this.code = code;
        this.message = message;
    }
}
