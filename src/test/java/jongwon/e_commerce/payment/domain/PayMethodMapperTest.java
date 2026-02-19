package jongwon.e_commerce.payment.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayMethodMapperTest {

    @Test
    @DisplayName("카드 -> PayMethod.CARD 로 매핑된다")
    void from_should_return_CARD() {
        PayMethod result = PayMethodMapper.from("카드");

        assertThat(result).isEqualTo(PayMethod.CARD);
    }

    @Test
    @DisplayName("계좌이체 -> PayMethod.TRANSFER 로 매핑된다")
    void from_should_return_TRANSFER() {
        PayMethod result = PayMethodMapper.from("계좌이체");

        assertThat(result).isEqualTo(PayMethod.TRANSFER);
    }

    @Test
    @DisplayName("가상계좌 -> PayMethod.VIRTUAL_ACCOUNT 로 매핑된다")
    void from_should_return_VIRTUAL_ACCOUNT() {
        PayMethod result = PayMethodMapper.from("가상계좌");

        assertThat(result).isEqualTo(PayMethod.VIRTUAL_ACCOUNT);
    }

    @Test
    @DisplayName("휴대폰 -> PayMethod.MOBILE 로 매핑된다")
    void from_should_return_MOBILE() {
        PayMethod result = PayMethodMapper.from("휴대폰");

        assertThat(result).isEqualTo(PayMethod.MOBILE);
    }

    @Test
    @DisplayName("간편결제 -> PayMethod.EASY_PAY 로 매핑된다")
    void from_should_return_EASY_PAY() {
        PayMethod result = PayMethodMapper.from("간편결제");

        assertThat(result).isEqualTo(PayMethod.EASY_PAY);
    }

}