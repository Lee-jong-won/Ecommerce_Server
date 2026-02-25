package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.application.approve.api.PaymentApprovalPGCaller;
import jongwon.e_commerce.payment.application.approve.result.PaymentCompletionProcessor;
import jongwon.e_commerce.payment.application.approve.validator.OrderValidatorBeforePGCall;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayDomainFactory;
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
    private final PaymentCompletionProcessor paymentCompletionProcessor;

    public void approvePayment(TossPaymentApproveRequest request){
        String orderId = request.getOrderId();
        long amount = request.getAmount();

        //1. 결제 전 주문 정보 검증
        Order order = orderValidatorBeforePGCall.validateOrderForPayment(orderId, amount);
        try {
            // 2. 결제 승인 api 호출
            TossPaymentApproveResponse response = paymentApprovalPGCaller.callPayApproveApi(request, UUID.randomUUID().toString());
            // 3. 주문 정보와, PG응답을 바탕으로 Pay 객체 생성
            Pay pay = PayDomainFactory.from(order, response);
            // 4-1. 결제 성공 시, 주문 상태 성공 처리 후, 후속 프로세스 진행
            order.markPaid();
            paymentCompletionProcessor.complete(pay);
        } catch(TossPaymentTimeoutException e){
            order.markPayTimeout();
        } catch(TossPaymentException e){
            order.markFailed();
        }
    }
}
