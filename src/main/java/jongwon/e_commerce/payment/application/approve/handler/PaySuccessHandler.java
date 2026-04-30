package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.order.application.OrderStockProcessor;
import jongwon.e_commerce.payment.application.PaymentService;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaySuccessHandler implements PayOutcomeHandler {

    private final OrderStockProcessor orderStockProcessor;
    private final PaymentService paymentService;

    @Override
    public boolean supports(PayApproveOutcome outcome) {
        return outcome instanceof PayApproveSuccess;
    }

    @Override
    @Transactional
    public void handle(Pay pay, PayApproveOutcome outcome) {
        PayApproveSuccess payApproveSuccess = (PayApproveSuccess)outcome;
        PayResult payResult = payApproveSuccess.getPayResult();

        log.info("SuccessHandler 작업 시작");

        // 1. 결제 결과 반영
        paymentService.updatePayResult(pay.getId(), payResult);

        // 2. 재고 감소
        orderStockProcessor.deductStockOf(pay.getOrder().getId());

        log.info("SuccessHandler 작업 종료");
    }
}
