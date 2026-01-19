package jongwon.e_commerce.payment.infra.toss;

import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class TossPaymentApproveResponse {
    private String status;        // DONE
    private String method;        // 카드
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;
}

