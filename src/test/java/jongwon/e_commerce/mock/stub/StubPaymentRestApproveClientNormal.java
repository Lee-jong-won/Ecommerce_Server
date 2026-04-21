package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.PaymentApproveClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;

public class StubPaymentRestApproveClientNormal implements PaymentApproveClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        return new TossPaymentApproveResponse(
                "휴대폰", "2024-02-13T12:18:14+09:00",
                new TossPaymentApproveResponse.MobilePhoneDto(
                        "01012345678",
                        "SETTLED",
                        "http://receipt.url"
                )
        );
    }


}
