package jongwon.e_commerce.order.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.jpa.entity.MemberEntity;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_id", columnList = "order_id")})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    @ManyToOne
    @JoinColumn(name = "fk_member_id", nullable = false)
    private MemberEntity memberEntity;

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
        orderEntity.memberEntity = MemberEntity.from(order.getMember());
        orderEntity.orderName = order.getOrderName();
        orderEntity.orderStatus = order.getOrderStatus();
        orderEntity.totalAmount = order.getTotalAmount();
        orderEntity.orderedAt = order.getOrderedAt();

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
                .member(memberEntity.toModel())
                .build();
    }

}
