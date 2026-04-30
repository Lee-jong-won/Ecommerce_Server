package jongwon.e_commerce.payment.infrastructure.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.unknown.ReadTimeout;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

class IOExceptionHandlerTest {

    jongwon.e_commerce.payment.infrastructure.gateway.exhandler.IOExceptionHandler IOExceptionHandler = new IOExceptionHandler();

    @Test
    void read_timeout이면_PayApproveTimeout을_반환한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("Read timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = IOExceptionHandler.handle(ex);

        // then
        assertThat(result).isInstanceOf(ReadTimeout.class);
    }

    @Test
    void connect_timeout이면_TEMPORARY_ERROR를_반환한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("connect timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = IOExceptionHandler.handle(ex);

        // then
        assertThat(result).isInstanceOf(ConnectionTimeout.class);
    }

    @Test
    void connection_Request_timeout이_발생하면_PayApproveFail을_반환한다(){
        // given
        ConnectionRequestTimeoutException cause = new ConnectionRequestTimeoutException();
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = IOExceptionHandler.handle(ex);

        // then
        assertThat(result).isInstanceOf(ConnectionRequestTimeout.class);
    }



}