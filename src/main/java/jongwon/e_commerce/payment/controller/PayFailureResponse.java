package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;

@Getter
public class PayFailureResponse extends PayApproveOutcomeResponse {
    private final String code;
    private final String message;
    public PayFailureResponse(PayStatus payStatus, String code, String message){
        super(payStatus);
        this.code = code;
        this.message = message;
    }
}
