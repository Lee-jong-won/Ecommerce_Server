package jongwon.e_commerce.payment.controller.dto.body;

import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public class PayFailBody implements PayResponseBody {
    private final String code;
    private final String message;
}
