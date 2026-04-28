package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.mock.stub.StubNicePaymentExecutor;
import jongwon.e_commerce.mock.stub.StubTossPaymentExecutor;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PGCallerTest {

    PGCaller pgCaller = new PGCaller(List.of(new StubTossPaymentExecutor(), new StubNicePaymentExecutor()));

    @Test
    void 토스페이먼츠_요청이_온_경우_성공적으로_처리한다(){
        // given
        String pgType= "TOSS";
        PayApproveAttempt payApproveAttempt = new PayApproveAttempt("paymentKey", "orderId", 1000);

        // when
        PayApproveOutcome payApproveOutcome = pgCaller.processPayApprove(pgType, payApproveAttempt);

        // then
        assertInstanceOf(PayApproveSuccess.class, payApproveOutcome);
    }

    @Test
    void 나이스페이먼츠_요청이_온_경우_성공적으로_처리한다(){
        // given
        String pgType= "NICE";
        PayApproveAttempt payApproveAttempt = new PayApproveAttempt("paymentKey", "orderId", 1000);

        // when
        PayApproveOutcome payApproveOutcome = pgCaller.processPayApprove(pgType, payApproveAttempt);

        // then
        assertInstanceOf(PayApproveSuccess.class, payApproveOutcome);
    }
}