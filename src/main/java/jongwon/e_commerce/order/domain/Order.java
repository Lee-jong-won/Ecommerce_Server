package jongwon.e_commerce.order.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.common.domain.BaseEntity;
import jongwon.e_commerce.order.exception.InvalidOrderStateException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "fk_member_id", nullable = false)
    private Long memberId;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "order_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    public static Order createOrder(Long memberId){
        Order order = new Order();
        order.memberId = memberId;
        order.orderedAt = LocalDateTime.now();
        return order;
    }

    //비즈니스 메소드
    public void setTotalAmount(List<OrderItem> orderItems){
        int sum = 0;
        for(OrderItem orderItem : orderItems){
            sum += orderItem.getOrderPrice();
        }
        this.totalAmount = sum;
    }

    public void setOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }

    public void markPaymentPending() {
        if (this.orderStatus != OrderStatus.CREATED) {
            throw new InvalidOrderStateException("CREATED 상태에서만 결제를 시작할 수 있습니다.");
        }
        setOrderStatus(OrderStatus.PAYMENT_PENDING);
    }

    public void markPaid() {
        if (this.orderStatus != OrderStatus.PAYMENT_PENDING) {
            throw new InvalidOrderStateException("PAYMENT_PENDING 상태에서만 결제 완료 처리 가능합니다.");
        }
        setOrderStatus(OrderStatus.PAID);
    }

    public void markFailed() {
        if (this.orderStatus != OrderStatus.PAYMENT_PENDING) {
            throw new InvalidOrderStateException("PAYMENT_PENDING 상태에서만 결제 실패 처리 가능합니다.");
        }
        setOrderStatus(OrderStatus.FAILED);
    }

    public void markCancel() {
        if (this.orderStatus == OrderStatus.PAID) {
            throw new InvalidOrderStateException("결제 완료된 주문은 취소할 수 없습니다.");
        } else if(this.orderStatus == OrderStatus.CANCELLED){
            throw new InvalidOrderStateException("이미 취소 처리가 완료된 주문입니다.");
        } else if(this.orderStatus == OrderStatus.FAILED){
            throw new InvalidOrderStateException("이미 실패한 결제입니다.");
        }
        setOrderStatus(OrderStatus.CANCELLED);
    }

    public boolean isPayable() {
        return this.orderStatus == OrderStatus.CREATED;
    }

}
