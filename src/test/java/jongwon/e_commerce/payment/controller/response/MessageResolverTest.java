package jongwon.e_commerce.payment.controller.response;

import jongwon.e_commerce.payment.exception.PayErrorCode;
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
}