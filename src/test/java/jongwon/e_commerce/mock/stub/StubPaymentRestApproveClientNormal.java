package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.PaymentApproveClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;

import java.time.OffsetDateTime;

public class StubPaymentRestApproveClientNormal implements PaymentApproveClient {

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


}
