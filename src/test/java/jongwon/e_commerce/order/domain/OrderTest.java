package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import jongwon.e_commerce.order.exception.NotOrderOwnerException;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.support.fixture.OrderFixture;
import jongwon.e_commerce.support.fixture.OrderItemFixture;
import jongwon.e_commerce.support.fixture.ProductFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void 주문상품들의_총금액을_정확히_계산한다() {
        // given
        List<OrderItem> orderItems = OrderItemFixture.createDefaultOrderItems();

        // when
        int totalAmount = Order.calculateTotalAmount(orderItems);

        // then
        assertThat(totalAmount).isEqualTo(250000);
    }

    @Test
    void Order가_정상적으로_생성된다() {
        // given
        Member member = Member.createMember(
                MemberCreate.builder()
                        .loginId("testUser")
                        .password("1234")
                        .memberName("홍길동")
                        .email("test@test.com")
                        .addr("서울")
                        .build()
        );
        List<OrderItem> orderItems = OrderItemFixture.createDefaultOrderItems();

        // when
        Order order = Order.createOrder(
                member,
                LocalDateTime.now(),
                "order-1",
                orderItems,
                "노트북 외 1건"
        );

        // then
        assertThat(order.getMember()).isEqualTo(member);
        assertThat(order.getOrderId()).isEqualTo("order-1");
        assertThat(order.getTotalAmount()).isEqualTo(250000);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED);

        // 연관관계 검증
        assertThat(orderItems.get(0).getOrder()).isEqualTo(order);
    }

    @Test
    void ORDERED_상태에서_결제_성공_처리가_가능하다() {
        // given
        Order order = OrderFixture.createDefaultOrder();

        // when
        order.paid();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void ORDERED_상태가_아니면_결제_성공_처리시_예외가_발생한다() {
        // given
        Order order = OrderFixture.createDefaultOrder();
        order.setOrderStatus(OrderStatus.CANCEL);

        // when & then
        assertThatThrownBy(order::paid)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void PAID_상태에서_주문_취소가_가능하다() {
        // given
        Order order = OrderFixture.createDefaultOrder();
        order.paid();

        // when
        order.markCancel();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    void PAID_상태가_아니면_취소시_예외가_발생한다() {
        // given
        Order order = OrderFixture.createDefaultOrder();

        // when & then
        assertThatThrownBy(order::markCancel)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void 주문의_소유자가_아닌_경우_예외가_발생한다(){
        // given
        Order order = OrderFixture.createDefaultOrder();
        Member member = Member.builder().
                memberId(2L).
                loginId("testUser2").
                password("1234").
                memberName("종원").
                email("test@test.com").
                addr("서울").
                build();

        // when && then
        assertThrows(NotOrderOwnerException.class , () -> order.validateOwner(member));
    }

    @Test
    void 주문_금액과_결제_금액이_다르면_예외가_발생한다(){
        // given
        Order order = OrderFixture.createDefaultOrder();

        // when && then
        assertThrows(InvalidAmountException.class, () -> order.validatePayAmount(10000L));
    }
}