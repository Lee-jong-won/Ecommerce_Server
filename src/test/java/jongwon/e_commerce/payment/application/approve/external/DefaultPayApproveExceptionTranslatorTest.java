package jongwon.e_commerce.payment.application.approve.external;

import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import java.net.SocketTimeoutException;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultPayApproveExceptionTranslatorTest {

    @Test
    void SocketTimeoutException이_포함된_경우_타임아웃으로_변환된다() {
        // given
        SocketTimeoutException socketTimeoutException = new SocketTimeoutException("timeout");

        ResourceAccessException resourceAccessException =
                new ResourceAccessException("I/O error", socketTimeoutException);

        DefaultPayApproveExceptionTranslator translator =
                new DefaultPayApproveExceptionTranslator();

        // when
        PayApproveOutcome outcome = translator.translate(resourceAccessException);

        // then
        assertThat(outcome).isInstanceOf(PayApproveTimeout.class);
    }

    @Test
    void 일반적인_예외는_실패로_변환된다() {
        // given
        RestClientException exception =
                new RestClientException("network error");

        DefaultPayApproveExceptionTranslator translator =
                new DefaultPayApproveExceptionTranslator();

        // when
        PayApproveOutcome outcome = translator.translate(exception);

        // then
        assertThat(outcome).isInstanceOf(PayApproveFail.class);
    }
}