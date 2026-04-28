package jongwon.e_commerce.payment.gateway.dto;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.gateway.dto.result.detail.MobilePaymentDetail;
import jongwon.e_commerce.payment.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;


public class PayResultResponseMapper {

    public static PayResult from(TossPaymentApproveResponse response){

        PayMethod method = PayMethodResolver.fromToss(response.getMethod());
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(response.getApprovedAt());
        String orderName = response.getOrderName();
        long amount = response.getAmount();

        // 상세 정보 변환 (분기 로직 집중)
        Map<String, Object> paymentDetailMap = switch (method) {
            case MOBILE -> Optional.ofNullable(MobilePaymentDetail.from(response.getMobilePhone()))
                    .map(MobilePaymentDetail::toMap)
                    .orElseGet(Collections::emptyMap);
            default -> Collections.emptyMap();
        };

        return PayResult.builder().
                payResultCommon(PayResult.PayResultCommon.
                        builder().approvedAt(offsetDateTime)
                        .payMethod(method).
                        amount(amount).
                        orderName(orderName).
                        build()).
                paymentDetail(paymentDetailMap).
                build();
    }

    // 나이스 페이먼츠 추가시 코드 작성
    // public static PayResult from(NicePaymentApproveReponse response){}

}
