package jongwon.e_commerce.payment.infra.toss;

import java.time.OffsetDateTime;

public class TossPaymentApproveResponse {
    private String status;        // DONE
    private String method;        // 카드
    private OffsetDateTime requestedAt;
    private OffsetDateTime approvedAt;
}

