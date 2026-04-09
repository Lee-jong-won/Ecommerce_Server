package jongwon.e_commerce.payment.controller.dto.body;

import jongwon.e_commerce.payment.domain.PayMethod;
import lombok.Builder;

@Builder
public class PaySuccessBody implements PayResponseBody {
    private final PayMethod payMethod;
    private final long payAmount;
}
