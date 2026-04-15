package jongwon.e_commerce.payment.application.approve.handler;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.result.unknown.ReadTimeout;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayTimeoutHandler implements PayOutcomeHandler {
    private final PaymentRepository paymentRepository;
    @Override
    public boolean supports(PayApproveOutcome outcome) {
        return outcome instanceof ReadTimeout;
    }

    @Override
    @Transactional
    public void handle(Pay pay, PayApproveOutcome outcome) {
        log.info("TIMEOUT HANDLER 작업 시작");

        pay.timeout();
        paymentRepository.save(pay);

        log.info("TIMEOUT HANDLER 작업 종료");
    }
}
