package jongwon.e_commerce.payment.controller.dto;

import jongwon.e_commerce.payment.controller.dto.body.PayResponseBody;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class PayApproveOutcomeResponse {
    private final PayResponseBody payResponseBody;
    private final HttpStatus httpStatus;
}
