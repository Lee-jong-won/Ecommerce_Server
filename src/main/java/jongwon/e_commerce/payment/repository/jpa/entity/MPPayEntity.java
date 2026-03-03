package jongwon.e_commerce.payment.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@Table(name = "mobile_phone_pay_detail")
@NoArgsConstructor
public class MPPayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "fk_pay_id")
    private PayEntity payEntity;

    @Column(name = "customer_mobile_phone")
    private String customerMobilePhone;

    @Column(name = "settlement_status")
    private String settlementStatus;

    @Column(name = "receipt_url")
    private String receiptUrl;

    public static MPPayEntity from(MPPay mpPay){
        MPPayEntity jpaEntity = new MPPayEntity();
        jpaEntity.customerMobilePhone = mpPay.getCustomerMobilePhone();
        jpaEntity.settlementStatus = mpPay.getSettlementStatus();
        jpaEntity.receiptUrl = mpPay.getReceiptUrl();
        jpaEntity.payEntity = PayEntity.from(mpPay.)
        return jpaEntity;
    }
}
