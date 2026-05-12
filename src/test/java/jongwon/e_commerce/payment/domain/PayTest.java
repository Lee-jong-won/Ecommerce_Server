package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import jongwon.e_commerce.support.fixture.OrderFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayTest {

    @Test
    void 결제정보가_정상적으로_생성된다() {
        // given
        Order order = OrderFixture.createDefaultOrder();

        // when
        PayRequest pay = PayRequest.from(order, "paymentKey-123", 250000L, PGType.TOSS);

        // then
        assertThat(pay.getOrder()).isEqualTo(order);
        assertThat(pay.getPaymentKey()).isEqualTo("paymentKey-123");
        assertThat(pay.getPayAmount()).isEqualTo(250000L);
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void PENDING_상태에서_결제_성공이_가능하다() {
        // given
        PayRequest payRequest = createPayRequest();

        // when
        payRequest.complete();

        // then
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.COMPLETE);
    }

    @Test
    void PENDING이_아닌_상태에서는_결제_성공시_예외가_발생한다() {
        // given
        PayRequest payRequest = createPayRequest();
        payRequest.setPayStatus(PayStatus.COMPLETE);

        // when & then
        assertThatThrownBy(payRequest::complete)
                .isInstanceOf(InvalidPayStatusException.class);
    }

    @Test
    void PENDING_상태에서_결제_실패가_가능하다() {
        // given
        PayRequest payRequest = createPayRequest();

        // when
        payRequest.businessFailed();;

        // then
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.BUSINESS_FAILED);
    }

    @Test
    void PENDING이_아닌_상태에서는_결제_실패시_예외가_발생한다() {
        // given
        PayRequest payRequest = createPayRequest();
        payRequest.setPayStatus(PayStatus.COMPLETE);

        // when & then
        assertThatThrownBy(payRequest::businessFailed)
                .isInstanceOf(InvalidPayStatusException.class);
    }

    @Test
    void PENDING_상태에서_타임아웃이_가능하다() {
        // given
        PayRequest payRequest = createPayRequest();

        // when
        payRequest.unknown();

        // then
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.UNKNOWN);
    }

    @Test
    void PENDING이_아닌_상태에서는_타임아웃시_예외가_발생한다() {
        // given
        PayRequest payRequest = createPayRequest();
        payRequest.setPayStatus(PayStatus.COMPLETE);

        // when & then
        assertThatThrownBy(payRequest::unknown)
                .isInstanceOf(InvalidPayStatusException.class);
    }

    @Test
    void COMPLETE_상태에서_환불이_가능하다() {
        // given
        PayRequest payRequest = createPayRequest();
        payRequest.setPayStatus(PayStatus.COMPLETE);

        // when
        payRequest.refund();

        // then
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.REFUND);
    }

    @Test
    void COMPLETE가_아닌_상태에서는_환불시_예외가_발생한다() {
        // given
        PayRequest payRequest = createPayRequest();

        // when & then
        assertThatThrownBy(payRequest::refund)
                .isInstanceOf(InvalidPayStatusException.class);
    }

    private PayRequest createPayRequest(){
        Order order = OrderFixture.createDefaultOrder();
        return PayRequest.from(order, "paymentKey", 250000L, PGType.TOSS);
    }
}