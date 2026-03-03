package jongwon.e_commerce.payment.application.approve.settlement.handler;

import jongwon.e_commerce.payment.controller.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayFailHandler implements PayOutcomeHandler {

    private final PaymentRepository paymentRepository;

    @Override
    public boolean supports(PayApproveOutcomeType type) {
        return type == PayApproveOutcomeType.FAIL;
    }

    @Override
    @Transactional
    public PayApproveOutcomeResponse handle(Pay pay, PayApproveOutcome outcome) {
        PayApproveFail payApproveFail = (PayApproveFail) outcome;
        paymentRepository.save(pay);

        return new PayApproveOutcomeResponse(
                PayStatus.FAILED,
                "PAYMENT_FAIL",
                "결제에 실패했습니다" // 결제 실패 유형별 세분화 처리 필요시, payApproveFail에서 정보 추출
        );
    }
}
