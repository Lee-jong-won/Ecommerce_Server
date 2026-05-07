package jongwon.e_commerce.payment.infrastructure.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.order.infrastructure.jpa.entity.OrderEntity;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pay_request")
@Getter
@NoArgsConstructor
public class PayRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_request_id")
    private Long payRequestId;

    @Column(name = "pay_amount", nullable = false)
    private long payAmount;

    @Column(name = "pay_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PayStatus payStatus;

    @Column(name = "payment_key", unique = true, nullable = false)
    private String paymentKey;

    @Column(name = "pg_type", nullable = false)
    private String pgType;

    @OneToOne
    @JoinColumn(name = "fk_order_id")
    private OrderEntity orderEntity;

    public static PayRequestEntity from(PayRequest payRequest){
        PayRequestEntity jpaEntity = new PayRequestEntity();

        jpaEntity.payRequestId = payRequest.getId();
        jpaEntity.payAmount = payRequest.getPayAmount();
        jpaEntity.payStatus = payRequest.getPayStatus();
        jpaEntity.paymentKey = payRequest.getPaymentKey();
        jpaEntity.pgType = payRequest.getPgType();

        return jpaEntity;
    }

    public PayRequest toModel(){
        return PayRequest.
                builder().
                id(payRequestId).
                payAmount(payAmount).
                paymentKey(paymentKey).
                payStatus(payStatus).
                pgType(pgType).
                order(orderEntity.toModel()).
                build();
    }
}
