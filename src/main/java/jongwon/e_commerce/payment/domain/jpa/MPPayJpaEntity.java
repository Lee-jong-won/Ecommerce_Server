package jongwon.e_commerce.payment.domain.jpa;

import jakarta.persistence.*;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.MPPay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.management.remote.JMXPrincipal;
import java.time.OffsetDateTime;

@Entity
@Getter
@Table(name = "mobile_phone_pay_detail")
@NoArgsConstructor
public class MPPayJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "fk_pay_id")
    private PayJpaEntity payJpaEntity;

    @Column(name = "customer_mobile_phone")
    private String customerMobilePhone;

    @Column(name = "settlement_status")
    private String settlementStatus;

    @Column(name = "receipt_url")
    private String receiptUrl;

    public static MPPayJpaEntity from(MPPay mpPay){
        MPPayJpaEntity jpaEntity = new MPPayJpaEntity();
        jpaEntity.customerMobilePhone = mpPay.getCustomerMobilePhone();
        jpaEntity.settlementStatus = mpPay.getSettlementStatus();
        jpaEntity.receiptUrl = mpPay.getReceiptUrl();
        return jpaEntity;
    }

    public MPPay toDomain(){
        Long payId = payJpaEntity.getPayId();
        String paymentKey = payJpaEntity.getPaymentKey();
        PayStatus payStatus = payJpaEntity.getPayStatus();
        PayMethod payMethod = payJpaEntity.getPayMethod();
        Long payAmount = payJpaEntity.getPayAmount();
        OffsetDateTime approvedAt = payJpaEntity.getApprovedAt();
        Order order = payJpaEntity.getOrder();

        return MPPay.builder().
                payId(payId).
                paymentKey(paymentKey).
                payStatus(payStatus).
                payMethod(payMethod).
                payAmount(payAmount).
                approvedAt(approvedAt).
                order(order).
                customerMobilePhone(customerMobilePhone).
                settlementStatus(settlementStatus).
                receiptUrl(receiptUrl).build();
    }
}
