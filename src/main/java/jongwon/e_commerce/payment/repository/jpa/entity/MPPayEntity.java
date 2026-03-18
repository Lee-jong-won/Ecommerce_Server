package jongwon.e_commerce.payment.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@Table(name = "mobile_phone_pay_detail")
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

    public void setPayEntity(PayEntity payEntity){
        this.payEntity = payEntity;
    }

    public static MPPayEntity from(MPPay mpPay){
        MPPayEntity mpPayEntity = new MPPayEntity();

        mpPayEntity.customerMobilePhone = mpPay.getCustomerMobilePhone();
        mpPayEntity.settlementStatus = mpPay.getSettlementStatus();
        mpPayEntity.receiptUrl = mpPay.getReceiptUrl();

        return mpPayEntity;
    }

    public Pay toModel(){
        MPPay mpPay = MPPay.builder()
                .id(id)
                .customerMobilePhone(customerMobilePhone)
                .receiptUrl(receiptUrl)
                .settlementStatus(settlementStatus)
                .build();

        return Pay.builder()
                .id(payEntity.getPayId())
                .payAmount(payEntity.getPayAmount())
                .payStatus(payEntity.getPayStatus())
                .payMethod(payEntity.getPayMethod())
                .approvedAt(payEntity.getApprovedAt())
                .paymentKey(payEntity.getPaymentKey())
                .order(payEntity.getOrderEntity().toModel())
                .paymentDetail(mpPay)
                .build();
    }
}
