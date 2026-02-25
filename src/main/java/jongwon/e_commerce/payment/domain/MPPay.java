package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@SuperBuilder
public class MPPay extends Pay {

    private String customerMobilePhone;

    private String settlementStatus;

    private String receiptUrl;

    public static MPPay create(Order order, TossPaymentApproveResponse response){
        // 결제 공통 정보
        String paymentKey = response.getPaymentKey();;
        PayMethod payMethod = PayMethodMapper.from(response.getMethod());
        long payAmount = response.getAmount();
        PayStatus payStatus = PayStatus.SUCCESS;
        OffsetDateTime approvedAt = response.getApprovedAt();

        // 핸드폰 결제 상세 정보
        TossPaymentApproveResponse.MobilePhoneDto mobilePhoneDto = response.getMobilePhone();
        String customerMobilePhone = mobilePhoneDto.getCustomerMobilePhone();
        String settlementStatus = mobilePhoneDto.getSettlementStatus();
        String receiptUrl = mobilePhoneDto.getReceiptUrl();

        return MPPay.builder().
                paymentKey(paymentKey).
                payAmount(payAmount).
                payMethod(payMethod).
                payStatus(payStatus).
                approvedAt(approvedAt).
                order(order). // 공통 정보 생성 완료
                customerMobilePhone(customerMobilePhone).
                settlementStatus(settlementStatus).
                receiptUrl(receiptUrl). //상세 정보 생성 완료
                build(); // 전체 결제 정보 생성
    }
}
