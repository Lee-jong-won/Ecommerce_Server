package jongwon.e_commerce.payment.toss.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TossPaymentCancelResponse {
    private List<CancelDto> cancels;
    @Getter
    @AllArgsConstructor
    public static class CancelDto {
        private String cancelReason;
        private OffsetDateTime canceledAt;
        private Long cancelAmount;
        private String transactionKey;
        private String cancelStatus;
    }
}
