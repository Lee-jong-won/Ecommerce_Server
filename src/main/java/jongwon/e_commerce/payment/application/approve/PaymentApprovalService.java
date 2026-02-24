package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.payment.application.approve.api.PaymentApprovalPGCaller;
import jongwon.e_commerce.payment.application.approve.result.OrderHandlerAfterPGFail;
import jongwon.e_commerce.payment.application.approve.result.OrderHandlerAfterPGSuccess;
import jongwon.e_commerce.payment.application.approve.result.OrderHandlerAfterPGTimeout;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentContext;
import jongwon.e_commerce.payment.application.approve.validator.OrderValidatorBeforePGCall;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovalService {

    // 결제 전 주문 정보 검증 부
    private final OrderValidatorBeforePGCall orderValidatorBeforePGCall;

    // 외부 api 호출부
    private final PaymentApprovalPGCaller paymentApprovalPGCaller;

    // 외부 api 호출 결과에 따른, 주문 결과 처리 부
    private final OrderHandlerAfterPGSuccess orderHandlerAfterPGSuccess;
    private final OrderHandlerAfterPGFail orderHandlerAfterPGFail;
    private final OrderHandlerAfterPGTimeout orderHandlerAfterPGTimeout;

    public void approvePayment(TossPaymentApproveRequest request){

        String orderId = request.getOrderId();
        long amount = request.getAmount();

        //1. 결제 전 주문 정보 검증
        orderValidatorBeforePGCall.validateOrderForPayment(orderId, amount);

        try {
            // 2. 결제 승인 api 호출
            TossPaymentApproveResponse response = paymentApprovalPGCaller.callPayApproveApi(request, UUID.randomUUID().toString());

            // 3. PG응답에서 주문에 대한 결제정보 추출
            PaymentContext paymentContext = response.toPaymentContext();

            // 4-1. 결제 성공 시, 정상 처리
            orderHandlerAfterPGSuccess.onSuccess(orderId, paymentContext);
        } catch(TossPaymentTimeoutException e){
            // 4-2. 타임아웃 발생 시, 예외 처리
            orderHandlerAfterPGTimeout.onTimeout(orderId);
        } catch(TossPaymentException e){
            // 4-3. 결제 실패 응답 시, 예외 처리
            orderHandlerAfterPGFail.onFail(orderId);
        }
    }
}
