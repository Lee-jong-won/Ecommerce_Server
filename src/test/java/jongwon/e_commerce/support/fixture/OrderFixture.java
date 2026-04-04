package jongwon.e_commerce.support.fixture;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderFixture {

    @Builder.Default
    private String orderId = "ORDER-DEFAULT";

    @Builder.Default
    private Member member = null; // 반드시 외부에서 주입 권장

    @Builder.Default
    private String orderName = "기본 주문";

    @Builder.Default
    private LocalDateTime orderedAt = LocalDateTime.now();

    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    @Builder.Default
    private long totalAmount = 10000L;

    public Order create() {
        return Order.builder()
                .orderId(orderId)
                .member(member)
                .orderName(orderName)
                .orderedAt(orderedAt)
                .orderStatus(orderStatus)
                .totalAmount(totalAmount)
                .build();
    }

    public static Order createDefaultOrder(){
        Member member = MemberFixture.builder().memberId(1L).build().create();
        List<OrderItem> orderItems = OrderItemFixture.createDefaultOrderItems();
        Order order = Order.createOrder(member,
                LocalDateTime.now(),
                "order-1",
                orderItems,
                "테스트 주문"
        );
        return order;
    }
}
