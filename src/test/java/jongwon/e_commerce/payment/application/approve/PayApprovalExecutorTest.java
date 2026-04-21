package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.mock.stub.*;
import jongwon.e_commerce.payment.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InvalidCard;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.outcome.unknown.ReadTimeout;
import jongwon.e_commerce.payment.toss.DefaultPayApproveExceptionTranslator;
import jongwon.e_commerce.payment.toss.PayApproveExceptionTranslator;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PayApprovalExecutorTest {

    PayApprovalExecutor payApprovalExecutor;
    PayApproveExceptionTranslator payApproveExceptionTranslator = new DefaultPayApproveExceptionTranslator(new ObjectMapper());

    @Test
    void 정상_응답이_돌아올_경우_PayApproveSuccess를_반환한다(){
        // given
        payApprovalExecutor = new PayApprovalExecutor(new StubPaymentRestApproveClientNormal(), payApproveExceptionTranslator);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        PayApproveOutcome payApproveOutcome = payApprovalExecutor.executePayApprove(request, UUID.randomUUID(). toString());
        PayApproveSuccess payApproveSuccess = (PayApproveSuccess) payApproveOutcome;

        // then
        assertInstanceOf(PayApproveSuccess.class, payApproveOutcome);
        assertNotNull(payApproveSuccess.getPayResult());
    }

    @Test
    void 카드_정보가_잘못됐을_경우_INVALID_CARD를_반환한다(){
        // given
        payApprovalExecutor = new PayApprovalExecutor(new StubPaymentRestApproveClientErrorResponse(), payApproveExceptionTranslator);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        PayApproveOutcome payApproveOutcome = payApprovalExecutor.executePayApprove(request, UUID.randomUUID().toString());

        // then
        assertInstanceOf(InvalidCard.class, payApproveOutcome);
    }

    @Test
    void ReadTimeout이_발생하는_경우_ReadTimeout을_반환한다(){
        // given
        payApprovalExecutor = new PayApprovalExecutor(new StubPaymentRestApproveClientReadTimeout(), payApproveExceptionTranslator);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        PayApproveOutcome payApproveOutcome = payApprovalExecutor.executePayApprove(request, UUID.randomUUID().toString());

        // then
        assertInstanceOf(ReadTimeout.class, payApproveOutcome);
    }

    @Test
    void ConnTimeout이_발생하는_경우_ConnTimeout을_반환한다(){
        // given
        payApprovalExecutor = new PayApprovalExecutor(new StubPaymentRestApproveClientConnTimeout(), payApproveExceptionTranslator);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        PayApproveOutcome payApproveOutcome = payApprovalExecutor.executePayApprove(request, UUID.randomUUID().toString());

        // then
        assertInstanceOf(ConnectionTimeout.class, payApproveOutcome);

    }

    @Test
    void 커넥션_요청_타임아웃이_발생하는_경우_ConnectionRequestTimeout을_반환한다(){
        // given
        payApprovalExecutor = new PayApprovalExecutor(new StubPaymentRestApproveClientConnRequestTimeout(), payApproveExceptionTranslator);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        PayApproveOutcome payApproveOutcome = payApprovalExecutor.executePayApprove(request, UUID.randomUUID().toString());

        // then
        assertInstanceOf(ConnectionRequestTimeout.class, payApproveOutcome);
    }

}