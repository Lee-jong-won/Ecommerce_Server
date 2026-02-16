package jongwon.e_commerce.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class TossPaymentApproveResponse {
    private String method;   // 카드
    private OffsetDateTime approvedAt; // 결제 승인 일자
    private String status; // 결제 상태
}

