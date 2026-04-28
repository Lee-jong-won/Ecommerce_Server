package jongwon.e_commerce.payment.gateway.dto;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PayResultResponseMapperTest {

    @Test
    @DisplayName("Toss 응답 DTO를 받아서 공통 PayResult로 정확히 변환한다")
    void shouldMapTossResponseToPayResult() {
        // given: Toss 응답 DTO 준비
        TossPaymentApproveResponse response = TossPaymentApproveResponse.builder().
                method("휴대폰").
                approvedAt("2026-04-27T00:00:00+09:00").
                mobilePhone(new TossPaymentApproveResponse.MobilePhoneDto(
                        "01012345678",
                        "COMPLETE",
                        "http://receipt.url")).
                amount(10000L).orderName("주문-1").build();

        // when: 매퍼 실행
        PayResult result = PayResultResponseMapper.from(response);

        // then: 관심사 검증
        assertAll(
                () -> assertThat(result.getPayResultCommon().getApprovedAt()).isNotNull(),
                () -> assertThat(result.getPayResultCommon().getPayMethod()).isEqualTo(PayMethod.MOBILE),
                () -> assertThat(result.getPayResultCommon().getAmount()).isEqualTo(10000L),
                () -> assertThat(result.getPayResultCommon().getOrderName()).isEqualTo("주문-1"),
                () -> assertThat(result.getPaymentDetail().get("phoneNumber")).isEqualTo("01012345678"),
                () -> assertThat(result.getPaymentDetail().get("settlementStatus")).isEqualTo("COMPLETE"),
                () -> assertThat(result.getPaymentDetail().get("receiptUrl")).isEqualTo("http://receipt.url")
        );
    }

    @Test
    @DisplayName("상세 정보가 없는 응답의 경우 빈 맵을 반환하여 에러를 방지한다")
    void shouldReturnEmptyMapWhenDetailIsNull() {
        // given: 상세 정보가 null인 DTO
        TossPaymentApproveResponse response = TossPaymentApproveResponse.builder().
                method("휴대폰").
                approvedAt("2026-04-27T00:00:00+09:00").orderName("...").
                mobilePhone(null).build();

        // when
        PayResult result = PayResultResponseMapper.from(response);

        // then
        assertThat(result.getPaymentDetail()).isEmpty();
    }

}