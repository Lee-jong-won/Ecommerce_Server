package jongwon.e_commerce.payment.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pay")
public class PayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payId;

    @OneToOne
    @JoinColumn(name = "fk_order_id")
    private OrderEntity orderEntity;

    @Column(name = "payment_id", nullable = false)
    private String paymentKey;

    @Column(name = "pay_method", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    @Column(name = "pay_amount", nullable = false)
    private long payAmount;

    @Column(name = "pay_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    public void setOrderEntity(OrderEntity orderEntity){this.orderEntity = orderEntity;}
    public void setPayStatus(PayStatus payStatus){
        this.payStatus = payStatus;
    }

    // 결제 취소 (승인 성공 후에만 가능)
    public void cancel() {
        if (payStatus != PayStatus.SUCCESS) {
            throw new InvalidPayStatusException(
                    "취소는 결제 성공 상태에서만 가능합니다. 현재 상태: " + payStatus
            );
        }
        setPayStatus(PayStatus.CANCELED);
    }

    public static PayEntity from(Pay pay){
        PayEntity jpaEntity = new PayEntity();
        jpaEntity.orderEntity = pay.getOrderEntity();
        jpaEntity.paymentKey = pay.getPaymentKey();
        jpaEntity.payMethod = pay.getPayMethod();
        jpaEntity.payAmount = pay.getPayAmount();
        jpaEntity.payStatus = pay.getPayStatus();
        jpaEntity.approvedAt = pay.getApprovedAt();
        return jpaEntity;
    }
}
