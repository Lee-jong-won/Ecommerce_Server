package jongwon.e_commerce.payment.toss.dto;

import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TossPaymentApproveResponseTest {

    @Test
    void 외부_PG응답의_결제유형이_핸드폰이면_핸드폰_상세정보가_만들어진다(){
        // given
        TossPaymentApproveResponse.MobilePhoneDto mobilePhoneDto = new TossPaymentApproveResponse.MobilePhoneDto("010-1234-5678",
                "정산완료",
                "https://naver.com");
        TossPaymentApproveResponse tossPaymentApproveResponse = new TossPaymentApproveResponse(
                10000, "order1", "paymentKey", "휴대폰", OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE",
                mobilePhoneDto);

        // when
        PaymentDetail paymentDetail = tossPaymentApproveResponse.convertToDetail();

        // then
        assertInstanceOf(MPPayDetail.class, paymentDetail);
    }

    @Test
    void 외부_PG응답의_결제유형이_우리_서비스에_등록되지_않았다면_예외가_발생한다(){
        // given
        TossPaymentApproveResponse tossPaymentApproveResponse = new TossPaymentApproveResponse(
                10000, "order1", "paymentKey", "카드", OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE",
                null);

        // when && then
        assertThrows(UnsupportedPayMethodException.class, () -> tossPaymentApproveResponse.convertToDetail());
    }

    @Test
    void 외부PG_응답이_정상적으로_PaymentContext로_변환된다(){
        // given
        TossPaymentApproveResponse.MobilePhoneDto mobilePhoneDto = new TossPaymentApproveResponse.MobilePhoneDto("010-1234-5678",
                "정산완료",
                "https://naver.com");
        TossPaymentApproveResponse tossPaymentApproveResponse = new TossPaymentApproveResponse(
                10000, "order1",
                "paymentKey", "휴대폰",
                OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE",
                mobilePhoneDto);

        // when
        PaymentDetail paymentDetail = tossPaymentApproveResponse.convertToDetail();
        PaymentContext paymentContext = tossPaymentApproveResponse.toPaymentContext(paymentDetail);

        // then
        assertEquals(tossPaymentApproveResponse.getAmount(), paymentContext.getAmount());
        assertEquals(tossPaymentApproveResponse.getOrderId(), paymentContext.getOrderId());
        assertEquals(tossPaymentApproveResponse.getPaymentKey(), paymentContext.getPaymentKey());
        assertEquals(tossPaymentApproveResponse.getMethod(), paymentContext.getMethod());
        assertEquals(tossPaymentApproveResponse.getApprovedAt(), paymentContext.getApprovedAt());
        assertEquals(paymentDetail, paymentContext.getPaymentDetail());
    }

}