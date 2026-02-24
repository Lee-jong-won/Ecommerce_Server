package jongwon.e_commerce.payment.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.payment.application.approve.result.context.MobilePhoneDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "mobile_phone_pay_detail")
@NoArgsConstructor
public class MobilePhonePay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "fk_pay_id")
    private Pay pay;

    @Column(name = "customer_mobile_phone")
    private String customerMobilePhone;

    @Column(name = "settlement_status")
    private String settlementStatus;

    @Column(name = "receipt_url")
    private String receiptUrl;

    public static MobilePhonePay create(Pay pay, MobilePhoneDetail mobilePhoneDetail){
        MobilePhonePay mobilePhonePay = new MobilePhonePay();
        mobilePhonePay.pay = pay;
        mobilePhonePay.customerMobilePhone = mobilePhoneDetail.getCustomerMobilePhone();
        mobilePhonePay.settlementStatus = mobilePhoneDetail.getSettlementStatus();
        mobilePhonePay.receiptUrl = mobilePhoneDetail.getReceiptUrl();
        return mobilePhonePay;
    }

    public void setId(long id){
        this.id = id;
    }
}
