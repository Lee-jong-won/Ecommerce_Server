package jongwon.e_commerce.payment.gateway.dto.result.detail;

import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MobilePaymentDetail {

    private final String mobilePhoneNumber;
    private final String settlementStatus;
    private final String receiptUrl;

    public static MobilePaymentDetail from(TossPaymentApproveResponse.MobilePhoneDto dto){
        if(dto == null)
            return null;
        return new MobilePaymentDetail(dto.getCustomerMobilePhone(), dto.getSettlementStatus(), dto.getReceiptUrl());
    }

    public Map<String, Object> toMap(){
        return Map.of("phoneNumber", mobilePhoneNumber,
                "settlementStatus", settlementStatus,
                "receiptUrl", receiptUrl);
    }

    //나이스 페이먼츠 추가시 코드 작성
    // public static MobilePaymentDetail from(NicePaymentApproveResponse.MobilePhoneDto dto){}
}
