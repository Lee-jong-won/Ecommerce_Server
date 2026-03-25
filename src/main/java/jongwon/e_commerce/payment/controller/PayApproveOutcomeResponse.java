package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public abstract class PayApproveOutcomeResponse {

    private final PayStatus payStatus;

    protected PayApproveOutcomeResponse(PayStatus payStatus){
        this.payStatus = payStatus;
    }

}
