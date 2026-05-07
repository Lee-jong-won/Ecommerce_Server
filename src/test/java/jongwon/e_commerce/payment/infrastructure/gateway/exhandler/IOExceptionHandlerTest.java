package jongwon.e_commerce.payment.infrastructure.gateway.exhandler;

import jongwon.e_commerce.payment.exception.PayTimeoutException;
import jongwon.e_commerce.payment.exception.PayServerException;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class IOExceptionHandlerTest {
    @Test
    void read_timeout이면_PayNetWorkException을_throw한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("Read timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when && then
        assertThrows(PayTimeoutException.class, () -> IOExceptionHandler.handle(ex));
    }

    @Test
    void connect_timeout이면_PayServerException을_throw한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("connect timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when && then
        assertThrows(PayServerException.class, () -> IOExceptionHandler.handle(ex));
    }

    @Test
    void connection_Request_timeout이_발생하면_PayServerException을_throw한다(){
        // given
        ConnectionRequestTimeoutException cause = new ConnectionRequestTimeoutException();
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // then
        assertThrows(PayServerException.class, () -> IOExceptionHandler.handle(ex));
    }

}