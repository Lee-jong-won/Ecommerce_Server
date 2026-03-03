package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PayApproveOutcomeResponse {
    private final PayStatus payStatus;
    private final String code;
    private final String message;
}
