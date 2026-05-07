package jongwon.e_commerce.payment.infrastructure.gateway;

import jongwon.e_commerce.mock.stub.*;
import jongwon.e_commerce.payment.domain.PGType;
import jongwon.e_commerce.payment.exception.PayClientException;
import jongwon.e_commerce.payment.exception.PayServerException;
import jongwon.e_commerce.payment.exception.PayTimeoutException;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
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
    void 정상_응답이_돌아올_경우_PayResult를_반환한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientNormal(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS",10000);

        // when
        PayResult payResult = commonPaymentExecutor.executePayApprove(request);

        // then
        assertNotNull(payResult);
    }

    @Test
    void 카드_정보가_잘못됐을_경우_PayClientException을_throw한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientErrorResponse(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS",10000);

        // when && then
        assertThrows(PayClientException.class, () -> commonPaymentExecutor.executePayApprove(request));
    }

    @Test
    void ReadTimeout이_발생하는_경우_PayTimeoutException을_throw한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientReadTimeout(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS", 10000);

        // when && then
        assertThrows(PayTimeoutException.class, () -> commonPaymentExecutor.executePayApprove(request));
    }

    @Test
    void ConnTimeout이_발생하는_경우_PayServerException을_throw한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientConnTimeout(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS",10000);

        // when && then
        assertThrows(PayServerException.class, () -> commonPaymentExecutor.executePayApprove(request));
    }

    @Test
    void 커넥션_요청_타임아웃이_발생하는_경우_ConnectionRequestTimeout을_반환한다(){
        // given
        commonPaymentExecutor = new CommonPaymentExecutor(new StubPaymentRestApproveClientConnRequestTimeout(),
                tossExceptionTranslator, PGType.TOSS);
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "TOSS", 10000);


        // when && then
        assertThrows(PayServerException.class, () -> commonPaymentExecutor.executePayApprove(request));
    }

}