package jongwon.e_commerce.payment.controller.response;

import jongwon.e_commerce.payment.domain.approve.outcome.fail.PayErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jongwon.e_commerce.payment.controller.response.MessageResolver.resolve;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MessageResolverTest {

    @Test
    @DisplayName("INSUFFICIENT_BALANCE -> 잔액 부족")
    void insufficientBalance() {
        String result = resolve(PayErrorCode.INSUFFICIENT_BALANCE);

        assertThat(result).isEqualTo("잔액 부족");
    }

    @Test
    @DisplayName("INVALID_CARD -> 카드 정보가 잘못 되었습니다")
    void invalidCard() {
        String result = resolve(PayErrorCode.INVALID_CARD);

        assertThat(result).isEqualTo("카드 정보가 잘못 되었습니다");
    }

    @Test
    @DisplayName("JSON_PARSING_ERROR -> JSON 파싱 에러")
    void jsonParsingError() {
        String result = resolve(PayErrorCode.JSON_PARSING_ERROR);

        assertThat(result).isEqualTo("JSON 파싱 에러");
    }

    @Test
    @DisplayName("INVALID_ERROR_RESPONSE -> 잘못된 에러 응답")
    void invalidErrorResponse() {
        String result = resolve(PayErrorCode.INVALID_ERROR_RESPONSE);

        assertThat(result).isEqualTo("잘못된 에러 응답");
    }

    @Test
    @DisplayName("UNKNOWN_ERROR_CODE -> 등록되지 않은 에러 코드")
    void unknownErrorCode() {
        String result = resolve(PayErrorCode.UNKNOWN_ERROR_CODE);

        assertThat(result).isEqualTo("등록되지 않은 에러 코드");
    }
}