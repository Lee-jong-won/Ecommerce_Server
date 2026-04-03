package jongwon.e_commerce.support.fixture;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.product.domain.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OrderItemFixture {

    @Builder.Default
    private Order order = null; // 외부에서 주입

    @Builder.Default
    private Product product = null; // 외부에서 주입

    @Builder.Default
    private String productName = "기본 상품";

    @Builder.Default
    private int orderPrice = 10000;

    @Builder.Default
    private int orderQuantity = 1;

    public OrderItem create() {
        return OrderItem.builder()
                .order(order)
                .product(product)
                .productName(productName)
                .orderPrice(orderPrice)
                .orderQuantity(orderQuantity)
                .build();
    }

    public static List<OrderItem> createDefaultOrderItems() {
        Product labTop = ProductFixture.createLaptop();
        Product mouse = ProductFixture.createMouse();
        return List.of(OrderItemFixture.builder().
                product(labTop).
                orderQuantity(2).
                productName(labTop.getProductName()).
                orderPrice(labTop.getProductPrice()).build().create(),
                OrderItemFixture.builder().
                        product(mouse).
                orderQuantity(1).
                productName(mouse.getProductName()).
                orderPrice(mouse.getProductPrice()).build().create());
    }
}
