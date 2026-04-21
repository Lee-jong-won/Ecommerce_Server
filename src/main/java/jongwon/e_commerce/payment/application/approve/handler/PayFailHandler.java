package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayFailHandler implements PayOutcomeHandler {

    private final PaymentRepository paymentRepository;

    @Override
    public boolean supports(PayApproveOutcome outcome) {
        return outcome instanceof PayApproveFail;
    }

    @Override
    @Transactional
    public void handle(Pay pay, PayApproveOutcome outcome) {
        log.info("Fail Handler 작업 시작");

        pay.failed();
        paymentRepository.save(pay);

        log.info("Fail Handler 작업 종료");
    }
}
