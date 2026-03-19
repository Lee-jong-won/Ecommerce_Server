package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void 주문상품들의_총금액을_정확히_계산한다() {
        // given
        Product product1 = Product.from("노트북", 100000);
        product1.setStatus(ProductStatus.SELLING);

        Product product2 = Product.from("마우스", 50000);
        product2.setStatus(ProductStatus.SELLING);

        OrderItem item1 = OrderItem.from(product1, 2); // 200000
        OrderItem item2 = OrderItem.from(product2, 1); // 50000

        List<OrderItem> items = List.of(item1, item2);

        // when
        int totalAmount = Order.calculateTotalAmount(items);

        // then
        assertThat(totalAmount).isEqualTo(250000);
    }

    @Test
    void Order가_정상적으로_생성된다() {
        // given
        Member member = Member.from(
                MemberCreate.builder()
                        .loginId("testUser")
                        .password("1234")
                        .memberName("홍길동")
                        .email("test@test.com")
                        .addr("서울")
                        .build()
        );

        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);

        OrderItem orderItem = OrderItem.from(product, 2);

        List<OrderItem> items = List.of(orderItem);

        // when
        Order order = Order.from(
                member,
                LocalDateTime.now(),
                "order-1",
                items,
                "노트북 외 1건"
        );

        // then
        assertThat(order.getMember()).isEqualTo(member);
        assertThat(order.getOrderId()).isEqualTo("order-1");
        assertThat(order.getTotalAmount()).isEqualTo(200000);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.ORDERED);

        // 연관관계 검증
        assertThat(orderItem.getOrder()).isEqualTo(order);
    }

    @Test
    void ORDERED_상태에서_결제_성공_처리가_가능하다() {
        // given
        Order order = createOrder();

        // when
        order.paid();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void ORDERED_상태가_아니면_결제_성공_처리시_예외가_발생한다() {
        // given
        Order order = createOrder();
        order.setOrderStatus(OrderStatus.CANCEL);

        // when & then
        assertThatThrownBy(order::paid)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void PAID_상태에서_주문_취소가_가능하다() {
        // given
        Order order = createOrder();
        order.paid();

        // when
        order.markCancel();

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    @Test
    void PAID_상태가_아니면_취소시_예외가_발생한다() {
        // given
        Order order = createOrder();

        // when & then
        assertThatThrownBy(order::markCancel)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    private Order createOrder() {
        Member member = Member.from(
                MemberCreate.builder()
                        .loginId("testUser")
                        .password("1234")
                        .memberName("홍길동")
                        .email("test@test.com")
                        .addr("서울")
                        .build()
        );

        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);

        OrderItem item = OrderItem.from(product, 1);

        Order order = Order.from(
                member,
                LocalDateTime.now(),
                "order-1",
                List.of(item),
                "테스트 주문"
        );
        return order;
    }
}