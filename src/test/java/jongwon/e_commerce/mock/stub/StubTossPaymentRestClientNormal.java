package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.TossPaymentClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.dto.TossPaymentCancelResponse;

import java.time.OffsetDateTime;

public class StubTossPaymentRestClientNormal implements TossPaymentClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        return new TossPaymentApproveResponse(
                "휴대폰", OffsetDateTime.now(),
                new TossPaymentApproveResponse.MobilePhoneDto(
                        "01012345678",
                        "SETTLED",
                        "http://receipt.url"
                )
        );
    }

    @Override
    public TossPaymentCancelResponse callPayCancelApi(String paymentKey, String cancelReason, String idempotencyKey) {
        return null;
    }
}
