package jongwon.e_commerce.payment.application.approve.external;

import jongwon.e_commerce.mock.stub.StubTossPaymentRestClientErrorResponse;
import jongwon.e_commerce.mock.stub.StubTossPaymentRestClientNormal;
import jongwon.e_commerce.mock.stub.StubTossPaymentRestClientTimeout;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PayApprovalExecutorTest {

    PayApprovalExecutor payApprovalExecutor;
    PayApproveExceptionTranslator payApproveExceptionTranslator = new DefaultPayApproveExceptionTranslator();

    @Test
    void 정상_응답이_돌아올_경우_PayApproveSuccess를_반환한다(){
        // given
        payApprovalExecutor = new PayApprovalExecutor(new StubTossPaymentRestClientNormal(), payApproveExceptionTranslator);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        PayApproveOutcome payApproveOutcome = payApprovalExecutor.executePayApprove(request);
        PayApproveSuccess payApproveSuccess = (PayApproveSuccess) payApproveOutcome;

        // then
        assertInstanceOf(PayApproveSuccess.class, payApproveOutcome);
        assertNotNull(payApproveSuccess.getPayResult());
    }

    @Test
    void 에러_응답이_돌아올_경우_PayApproveFail을_반환한다(){
        // given
        payApprovalExecutor = new PayApprovalExecutor(new StubTossPaymentRestClientErrorResponse(), payApproveExceptionTranslator);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        PayApproveOutcome payApproveOutcome = payApprovalExecutor.executePayApprove(request);

        // then
        assertInstanceOf(PayApproveFail.class, payApproveOutcome);
    }

    @Test
    void 타임아웃이_발생하는_경우_PayApprovalTimeout을_반환한다(){
        // given
        payApprovalExecutor = new PayApprovalExecutor(new StubTossPaymentRestClientTimeout(), payApproveExceptionTranslator);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        PayApproveOutcome payApproveOutcome = payApprovalExecutor.executePayApprove(request);

        // then
        assertInstanceOf(PayApproveTimeout.class, payApproveOutcome);
    }

}