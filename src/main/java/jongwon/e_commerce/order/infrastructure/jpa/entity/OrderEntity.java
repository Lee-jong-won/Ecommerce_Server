package jongwon.e_commerce.order.infrastructure.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.member.infrastructure.jpa.entity.MemberEntity;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", unique = true)
    private String orderId;

    @Column(name = "fk_member_id", nullable = false)
    private Long memberId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderItemEntity> orderItemEntities = new ArrayList<>();

    @Column(name = "order_name", nullable = false)
    private String orderName;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "order_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false)
    private long totalAmount;

    public static OrderEntity from(Order order){

        OrderEntity orderEntity = new OrderEntity();

        orderEntity.id = order.getId();
        orderEntity.orderId = order.getOrderId();
        orderEntity.memberId = order.getMemberId();
        orderEntity.orderName = order.getOrderName();
        orderEntity.orderStatus = order.getOrderStatus();
        orderEntity.totalAmount = order.getTotalAmount();
        orderEntity.orderedAt = order.getOrderedAt();
        orderEntity.orderItemEntities.addAll(
                order.getOrderItems()
                        .stream()
                        .map(OrderItemEntity::from)
                        .toList()
        );

        return orderEntity;
    }

    public Order toModel(){
        return Order.builder()
                .id(id)
                .orderId(orderId)
                .orderedAt(orderedAt)
                .orderName(orderName)
                .totalAmount(totalAmount)
                .orderStatus(orderStatus)
                .orderItems(orderItemEntities.stream().map(OrderItemEntity::toModel).toList())
                .memberId(memberId)
                .build();
    }

}
