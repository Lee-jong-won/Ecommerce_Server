package jongwon.e_commerce.payment.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "mppay")
@NoArgsConstructor
public class MPPayEntity implements PayDetailEntity {

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
        MPPayEntity mpPayEntity = new MPPayEntity();

        mpPayEntity.id = mpPay.getId();
        mpPayEntity.payEntity = PayEntity.from(mpPay.getPay());
        mpPayEntity.customerMobilePhone = mpPay.getCustomerMobilePhone();
        mpPayEntity.settlementStatus = mpPay.getSettlementStatus();
        mpPayEntity.receiptUrl = mpPay.getReceiptUrl();

        return mpPayEntity;
    }

    public MPPay toModel(){
        return MPPay.builder()
                .id(id)
                .pay(payEntity.toModel())
                .customerMobilePhone(customerMobilePhone)
                .receiptUrl(receiptUrl)
                .settlementStatus(settlementStatus)
                .build();
    }
}
