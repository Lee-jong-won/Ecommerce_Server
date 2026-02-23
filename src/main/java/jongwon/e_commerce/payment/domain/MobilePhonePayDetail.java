package jongwon.e_commerce.payment.domain;

import jakarta.persistence.*;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "mobile_phone_pay_detail")
@NoArgsConstructor
public class MobilePhonePayDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fk_pay_id")
    private Long fkPayId;

    @Column(name = "customer_mobile_phone")
    private String customerMobilePhone;

    @Column(name = "settlement_status")
    private String settlementStatus;

    @Column(name = "receipt_url")
    private String receiptUrl;

    public static MobilePhonePayDetail create(Long fkPayId,
                                              TossPaymentApproveResponse.MobilePhoneDto dto){
        MobilePhonePayDetail mobilePhonePayDetail = new MobilePhonePayDetail();
        mobilePhonePayDetail.fkPayId = fkPayId;
        mobilePhonePayDetail.customerMobilePhone = dto.getCustomerMobilePhone();
        mobilePhonePayDetail.settlementStatus = dto.getSettlementStatus();
        mobilePhonePayDetail.receiptUrl = dto.getReceiptUrl();
        return mobilePhonePayDetail;
    }

    public void setId(long id){
        this.id = id;
    }
}
