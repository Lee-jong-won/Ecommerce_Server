package jongwon.e_commerce.payment.controller.response;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.result.fail.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.result.ignore.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.result.ignore.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.result.ignore.UnknownRestClientError;
import jongwon.e_commerce.payment.domain.approve.result.success.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.result.unknown.ReadTimeout;
import org.springframework.http.ResponseEntity;

public class PayOutcomeHttpMapper {
    public static ResponseEntity<?> toResponse(PayApproveOutcome payApproveOutcome){
        if(payApproveOutcome instanceof PayApproveSuccess s) {
            PayResult result = s.getPayResult();
            Pay pay = result.getPaymentDetail().getPay();
            Order order = pay.getOrder();
            return ResponseEntity.ok(
                    PayApproveSuccessResponse.builder().
                    payAmount(pay.getPayAmount()).
                    orderPrice(order.getTotalAmount()).
                    orderName(order.getOrderName()));
        }

        if(payApproveOutcome instanceof PayApproveFail f){
            return ResponseEntity.badRequest().body(PayApproveFailResponse.builder().
                    code(f.errorCode().name()).
                    message(MessageResolver.resolve(f.errorCode())));
        }

        if(payApproveOutcome instanceof ConnectionTimeout ||
                payApproveOutcome instanceof ReadTimeout ||
                    payApproveOutcome instanceof UnknownRestClientError){
            return ResponseEntity.status(504).body(PayApproveFailResponse.builder().
                    code("NETWORK_ERROR").message("일시적인 네트워크 오류입니다.").build());
        }

        if(payApproveOutcome instanceof ConnectionRequestTimeout){
            return ResponseEntity.status(503).body(PayApproveFailResponse.builder().
                    code("NO_CONNECTION_TO_USE").message("서버 리소스 자원이 부족합니다.").build());
        }

        return ResponseEntity.status(500).body(PayApproveFailResponse.
                builder().
                code("UNKNOWN").
                message("알 수 없는 오류").build());
    }
}
