package jongwon.e_commerce.payment.toss.dto;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayResult;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TossPaymentApproveResponseTest {
    @Test
    void Toss응답이_PayResult로_정상_변환된다() {
        // given
        TossPaymentApproveResponse.MobilePhoneDto mobilePhone =
                new TossPaymentApproveResponse.MobilePhoneDto(
                        "01012345678",
                        "SETTLED",
                        "http://receipt.url"
                );

        TossPaymentApproveResponse response =
                new TossPaymentApproveResponse(
                        "휴대폰",
                        "2024-02-13T12:18:14+09:00",
                        mobilePhone
                );

        // when
        PayResult payResult = response.toPayResult();
        PayResult.PayResultCommon payResultCommon = payResult.getPayResultCommon();

        // then
        assertThat(payResultCommon.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(payResultCommon.getApprovedAt()).isEqualTo("2024-02-13T12:18:14+09:00");
        assertThat(payResult.getPaymentDetail()).isInstanceOf(MPPay.class);
    }

    @Test
    void MOBILE_결제수단이면_PaymentDetail이_정상적으로_생성된다() {
        // given
        TossPaymentApproveResponse.MobilePhoneDto mobilePhone =
                new TossPaymentApproveResponse.MobilePhoneDto(
                        "01012345678",
                        "SETTLED",
                        "http://receipt.url"
                );

        TossPaymentApproveResponse response =
                new TossPaymentApproveResponse(
                        "휴대폰",
                        "2024-02-13T12:18:14+09:00",
                        mobilePhone
                );

        // when
        MPPay mpPay = (MPPay) response.extractPaymentDetail();

        // then
        assertThat(mpPay.getCustomerMobilePhone()).isEqualTo("01012345678");
        assertThat(mpPay.getReceiptUrl()).isEqualTo("http://receipt.url");
        assertThat(mpPay.getSettlementStatus()).isEqualTo("SETTLED");
    }

    @Test
    void 지원하지_않는_결제수단이면_예외가_발생한다() {
        // given
        TossPaymentApproveResponse response =
                new TossPaymentApproveResponse(
                        "UNKNOWN",
                        "2024-02-13T12:18:14+09:00",
                        null
                );

        // when & then
        assertThatThrownBy(response::extractPaymentDetail)
                .isInstanceOf(UnsupportedPayMethodException.class);
    }
}