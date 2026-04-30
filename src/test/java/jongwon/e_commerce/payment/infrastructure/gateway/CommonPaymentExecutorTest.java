package jongwon.e_commerce.payment.infrastructure.gateway;

import jongwon.e_commerce.mock.stub.*;
import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InvalidCard;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.none.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.none.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.outcome.unknown.ReadTimeout;
import jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler.TossErrorResponseHandler;
import jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler.TossExceptionTranslator;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

class CommonPaymentExecutorTest {

    TossExceptionTranslator tossExceptionTranslator = new TossExceptionTranslator(
            new TossErrorResponseHandler(new ObjectMapper()));
    CommonPaymentExecutor commonPaymentExecutor;

    @Test
    void 정상_응답이_돌아올_경우_PayApproveSuccess를_반환한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientNormal(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS",10000);

        // when
        PayApproveOutcome payApproveOutcome = commonPaymentExecutor.executePayApprove(request);
        PayApproveSuccess payApproveSuccess = (PayApproveSuccess) payApproveOutcome;

        // then
        assertInstanceOf(PayApproveSuccess.class, payApproveOutcome);
        assertNotNull(payApproveSuccess.getPayResult());
    }

    @Test
    void 카드_정보가_잘못됐을_경우_INVALID_CARD를_반환한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientErrorResponse(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS",10000);

        // when
        PayApproveOutcome payApproveOutcome = commonPaymentExecutor.executePayApprove(request);

        // then
        assertInstanceOf(InvalidCard.class, payApproveOutcome);
    }

    @Test
    void ReadTimeout이_발생하는_경우_ReadTimeout을_반환한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientReadTimeout(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS", 10000);

        // when
        PayApproveOutcome payApproveOutcome = commonPaymentExecutor.executePayApprove(request);

        // then
        assertInstanceOf(ReadTimeout.class, payApproveOutcome);
    }

    @Test
    void ConnTimeout이_발생하는_경우_ConnTimeout을_반환한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientConnTimeout(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS",10000);

        // when
        PayApproveOutcome payApproveOutcome = commonPaymentExecutor.executePayApprove(request);

        // then
        assertInstanceOf(ConnectionTimeout.class, payApproveOutcome);

    }

    @Test
    void 커넥션_요청_타임아웃이_발생하는_경우_ConnectionRequestTimeout을_반환한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientConnRequestTimeout(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS", 10000);


        // when
        PayApproveOutcome payApproveOutcome = commonPaymentExecutor.executePayApprove(request);

        // then
        assertInstanceOf(ConnectionRequestTimeout.class, payApproveOutcome);
    }

}