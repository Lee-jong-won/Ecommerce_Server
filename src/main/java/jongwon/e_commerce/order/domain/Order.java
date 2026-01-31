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

    @Column(name = "pay_order_id")
    private String payOrderId;

    @Column(name = "fk_member_id", nullable = false)
    private Long memberId;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "order_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false)
    private long totalAmount;

    public static Order createOrder(Long memberId){
        Order order = new Order();
        order.memberId = memberId;
        order.payOrderId = OrderIdGenerator.generate();
        order.orderStatus = OrderStatus.CREATED;
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

    public void markPaymentPending(){
        if(this.orderStatus != OrderStatus.CREATED) {
            throw new InvalidOrderStateException("CREATED 상태에서만 결제중으로 변경 가능합니다.");
        }
        setOrderStatus(OrderStatus.PAYMENT_PENDING);
    }


    //PG로부터 결제 승인 성공 응답을 받을 시
    public void markPaid() {
        if (this.orderStatus != OrderStatus.PAYMENT_PENDING) {
            throw new InvalidOrderStateException("PAYMENT_PENDING 상태에서만 결제 승인 완료 처리 가능합니다.");
        }
        setOrderStatus(OrderStatus.PAID);
    }

    //PG로부터 결제 승인 실패 응답을 받을 시
    public void markFailed() {
        if (this.orderStatus != OrderStatus.PAYMENT_PENDING) {
            throw new InvalidOrderStateException("PAYMENT_PENDING 상태 에서만 결제 승인 실패 처리 가능합니다.");
        }
        setOrderStatus(OrderStatus.FAILED);
    }

    //PG사 정책에 의해 결제가 만료되었을 시
    public void markExpired() {
        if (!isExpiredable()) {
            throw new InvalidOrderStateException(
                    "만료될 수 없는 주문 상태입니다. 현재 상태: " + orderStatus
            );
        }
        setOrderStatus(OrderStatus.EXPIRED);
    }

    //고객에 의해 주문이 취소 되었을 시
    public void markCancel(){
        if(this.orderStatus != OrderStatus.PAID){
            throw new InvalidOrderStateException("결제가 완료된 상태 에서만 취소 처리 가능합니다.");
        }
        setOrderStatus(OrderStatus.CANCELLED);
    }

    private boolean isExpiredable() {
        return orderStatus == OrderStatus.PAYMENT_PENDING;
    }
}
