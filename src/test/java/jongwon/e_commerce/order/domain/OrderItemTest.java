package jongwon.e_commerce.order.domain;

import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    void Product로부터_OrderItem이_정상적으로_생성된다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);

        // when
        OrderItem orderItem = OrderItem.createOrderItem(product, 2);

        // then
        assertThat(orderItem.getProduct()).isEqualTo(product);
        assertThat(orderItem.getProductName()).isEqualTo("노트북");
        assertThat(orderItem.getOrderPrice()).isEqualTo(100000);
        assertThat(orderItem.getOrderQuantity()).isEqualTo(2);
    }

    @Test
    void 주문금액은_가격과_수량을_곱한_값이다() {
        // given
        Product product = Product.from("노트북", 100000);
        product.setStatus(ProductStatus.SELLING);

        OrderItem orderItem = OrderItem.createOrderItem(product, 3);

        // when
        int amount = orderItem.calculateAmount();

        // then
        assertThat(amount).isEqualTo(300000);
    }

}