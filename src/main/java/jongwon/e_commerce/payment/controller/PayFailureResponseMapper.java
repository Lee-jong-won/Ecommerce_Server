package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PayFailureResponseMapper {

    public PayApproveOutcomeResponse toPayFailureResponse(PayApproveFail payApproveFail){
        HttpStatus httpStatus;
        switch (payApproveFail.getPayErrorCode()) {
            case CONNECTION_TIMEOUT -> httpStatus = HttpStatus.GATEWAY_TIMEOUT; // 504
            case HTTP_CLIENT_POOL_TIMEOUT,
                    DB_POOL_TIMEOUT -> httpStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503
            case UNKNOWN -> httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // 500
            default -> httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return payApproveFail.toPayFailureResponse(payApproveFail.getPayErrorCode().toString(), payApproveFail.getMessage(), httpStatus);
    }
}
