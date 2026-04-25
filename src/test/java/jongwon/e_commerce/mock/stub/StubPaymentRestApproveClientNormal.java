package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.gateway.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.PaymentApproveClient;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;

public class StubPaymentRestApproveClientNormal implements PaymentApproveClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request) {
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
