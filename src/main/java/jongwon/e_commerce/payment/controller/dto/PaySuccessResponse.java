package jongwon.e_commerce.payment.controller.dto;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;

@Getter
public class PaySuccessResponse extends PayApproveOutcomeResponse{
    private final PayMethod payMethod;
    private final long payAmount;
    public PaySuccessResponse(PayStatus payStatus, PayMethod payMethod, long payAmount){
        super(payStatus);
        this.payMethod = payMethod;
        this.payAmount = payAmount;
    }

}
