package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.support.fixture.ProductFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    void Product로부터_OrderItem이_정상적으로_생성된다() {
        // given
        Product product = ProductFixture.builder().
                productStatus(ProductStatus.SELLING).
                build().
                create();

        // when
        OrderItem orderItem = OrderItem.createOrderItem(product, 2);

        // then
        assertThat(orderItem.getProduct()).isEqualTo(product);
        assertThat(orderItem.getProductName()).isEqualTo("기본 상품");
        assertThat(orderItem.getOrderPrice()).isEqualTo(10000);
        assertThat(orderItem.getOrderQuantity()).isEqualTo(2);
    }

    @Test
    void 주문금액은_가격과_수량을_곱한_값이다() {
        // given
        Product product = ProductFixture.builder().
                productStatus(ProductStatus.SELLING).
                build().
                create();
        OrderItem orderItem = OrderItem.createOrderItem(product, 3);

        // when
        int amount = orderItem.calculateAmount();

        // then
        assertThat(amount).isEqualTo(30000);
    }

}