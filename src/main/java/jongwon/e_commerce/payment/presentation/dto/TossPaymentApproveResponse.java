package jongwon.e_commerce.payment.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class TossPaymentApproveResponse {
    private String method;        // 카드
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;
}

