package jongwon.e_commerce.payment.infrastructure.jpa.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jongwon.e_commerce.order.infrastructure.jpa.entity.OrderEntity;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "pay")
public class PayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long payId;

    @Column(name = "pay_method", length = 50)
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    @Column(name = "pay_amount", nullable = false)
    private long payAmount;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Type(JsonType.class)
    @Column(name = "payment_detail", columnDefinition = "json")
    private Map<String, Object> paymentDetail;

    @OneToOne
    @JoinColumn(name = "fk_pay_request_id")
    private PayRequestEntity payRequestEntity;

    @Column(name = "created_at", nullable = false, updatable = false,
            insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public static PayEntity from(Pay pay){
        PayEntity jpaEntity = new PayEntity();

        jpaEntity.payId = pay.getId();
        jpaEntity.payMethod = pay.getPayMethod();
        jpaEntity.payAmount = pay.getPayAmount();
        jpaEntity.approvedAt = pay.getApprovedAt();
        jpaEntity.paymentDetail = pay.getPaymentDetail();
        jpaEntity.payRequestEntity = PayRequestEntity.from(pay.getPayRequest());

        return jpaEntity;
    }

    public Pay toModel(){
        return Pay.builder()
                .id(payId)
                .payMethod(payMethod)
                .payAmount(payAmount)
                .approvedAt(approvedAt)
                .createdAt(createdAt)
                .paymentDetail(paymentDetail)
                .payRequest(payRequestEntity.toModel())
                .build();
    }
}
