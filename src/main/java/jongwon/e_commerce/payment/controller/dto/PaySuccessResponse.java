package jongwon.e_commerce.payment.controller.dto;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PaySuccessResponse extends PayApproveOutcomeResponse{
    private final PayMethod payMethod;
    private final long payAmount;
    public PaySuccessResponse(HttpStatus httpStatus,
                              PayStatus payStatus,
                              PayMethod payMethod,
                              long payAmount){
        super(payStatus, httpStatus);
        this.payMethod = payMethod;
        this.payAmount = payAmount;
    }

}
