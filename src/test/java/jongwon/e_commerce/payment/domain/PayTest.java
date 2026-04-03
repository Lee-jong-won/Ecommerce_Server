package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.support.fixture.OrderFixture;
import jongwon.e_commerce.support.fixture.OrderItemFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PayTest {

    @Test
    void 결제정보가_정상적으로_생성된다() {
        // given
        Order order = OrderFixture.createDefaultOrder();

        // when
        Pay pay = Pay.from(order, "paymentKey-123", 250000L);

        // then
        assertThat(pay.getOrder()).isEqualTo(order);
        assertThat(pay.getPaymentKey()).isEqualTo("paymentKey-123");
        assertThat(pay.getPayAmount()).isEqualTo(250000L);
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void PENDING_상태에서_결제_성공이_가능하다() {
        // given
        Pay pay = createPay();

        // when
        pay.comeplete();

        // then
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.COMPLETE);
    }

    @Test
    void PENDING이_아닌_상태에서는_결제_성공시_예외가_발생한다() {
        // given
        Pay pay = createPay();
        pay.setPayStatus(PayStatus.COMPLETE);

        // when & then
        assertThatThrownBy(pay::comeplete)
                .isInstanceOf(InvalidPayStatusException.class);
    }

    @Test
    void PENDING_상태에서_결제_실패가_가능하다() {
        // given
        Pay pay = createPay();

        // when
        pay.failed();

        // then
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

    @Test
    void PENDING이_아닌_상태에서는_결제_실패시_예외가_발생한다() {
        // given
        Pay pay = createPay();
        pay.setPayStatus(PayStatus.COMPLETE);

        // when & then
        assertThatThrownBy(pay::failed)
                .isInstanceOf(InvalidPayStatusException.class);
    }

    @Test
    void PENDING_상태에서_타임아웃이_가능하다() {
        // given
        Pay pay = createPay();

        // when
        pay.timeout();

        // then
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.TIME_OUT);
    }

    @Test
    void PENDING이_아닌_상태에서는_타임아웃시_예외가_발생한다() {
        // given
        Pay pay = createPay();
        pay.setPayStatus(PayStatus.COMPLETE);

        // when & then
        assertThatThrownBy(pay::timeout)
                .isInstanceOf(InvalidPayStatusException.class);
    }

    @Test
    void COMPLETE_상태에서_환불이_가능하다() {
        // given
        Pay pay = createPay();
        pay.setPayStatus(PayStatus.COMPLETE);

        // when
        pay.refund();

        // then
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.REFUND);
    }

    @Test
    void COMPLETE가_아닌_상태에서는_환불시_예외가_발생한다() {
        // given
        Pay pay = createPay();

        // when & then
        assertThatThrownBy(pay::refund)
                .isInstanceOf(InvalidPayStatusException.class);
    }


    @Test
    void 결제_공통정보가_Pay에_정상적으로_반영된다() {
        // given
        Pay originalPay = createPay();

        PayMethod method = PayMethod.CARD;
        OffsetDateTime approvedAt = OffsetDateTime.now();

        PayResult.PayResultCommon payResultCommon = PayResult.PayResultCommon.builder().
                payMethod(method).
                approvedAt(approvedAt).
                build();

        // when
        Pay resultPay = originalPay.reflectPaySuccess(payResultCommon);

        // then
        // 기존 값 유지
        assertThat(resultPay.getPaymentKey()).isEqualTo(originalPay.getPaymentKey());
        assertThat(resultPay.getPayAmount()).isEqualTo(originalPay.getPayAmount());
        assertThat(resultPay.getPayStatus()).isEqualTo(originalPay.getPayStatus());

        // PayResult 값 반영
        assertThat(resultPay.getPayMethod()).isEqualTo(method);
        assertThat(resultPay.getApprovedAt()).isEqualTo(approvedAt);
    }

    private Pay createPay(){
        Order order = OrderFixture.createDefaultOrder();
        return Pay.from(order, "paymentKey", 250000L);
    }
}