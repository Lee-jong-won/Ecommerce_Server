package jongwon.e_commerce.payment.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.exception.InvalidPayStatusException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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

    @Column(name = "pay_method", length = 50)
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    @Column(name = "pay_amount", nullable = false)
    private long payAmount;

    @Column(name = "pay_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false,
            insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, updatable = false,
            insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public static PayEntity from(Pay pay){
        PayEntity jpaEntity = new PayEntity();

        jpaEntity.payId = pay.getId();
        jpaEntity.orderEntity = OrderEntity.from(pay.getOrder());
        jpaEntity.paymentKey = pay.getPaymentKey();
        jpaEntity.payMethod = pay.getPayMethod();
        jpaEntity.payAmount = pay.getPayAmount();
        jpaEntity.payStatus = pay.getPayStatus();
        jpaEntity.approvedAt = pay.getApprovedAt();
        jpaEntity.createdAt = pay.getCreatedAt();
        jpaEntity.updatedAt = pay.getUpdatedAt();

        return jpaEntity;
    }

    public Pay toModel(){
        return Pay.builder()
                .id(payId)
                .order(orderEntity.toModel())
                .paymentKey(paymentKey)
                .payMethod(payMethod)
                .payAmount(payAmount)
                .payStatus(payStatus)
                .approvedAt(approvedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
