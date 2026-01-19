package jongwon.e_commerce.payment.presentation.dto;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class TossPaymentApproveResponse {
    private String method;        // 카드
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;
}

