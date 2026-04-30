package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentClient;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayResultResponseMapper;
import jongwon.e_commerce.payment.infrastructure.gateway.toss.dto.TossPaymentApproveResponse;

public class StubPaymentRestApproveClientNormal implements PaymentClient {

    @Override
    public PayApproveOutcome callPayApprovalApi(PayApproveAttempt request) {
        return new PayApproveSuccess(PayResultResponseMapper.from(TossPaymentApproveResponse.builder().
                method("휴대폰").
                approvedAt("2024-02-13T12:18:14+09:00").
                orderName("주문-1").
                amount(10000L).
                mobilePhone(new TossPaymentApproveResponse.MobilePhoneDto(
                        "01012345678",
                        "SETTLED",
                        "http://receipt.url"
                )).build()));
    }


}
