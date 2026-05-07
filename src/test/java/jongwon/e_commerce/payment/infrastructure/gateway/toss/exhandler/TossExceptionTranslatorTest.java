package jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler;

import jongwon.e_commerce.payment.exception.PayApproveException;
import jongwon.e_commerce.payment.exception.PayClientException;
import jongwon.e_commerce.payment.exception.PayErrorCode;
import jongwon.e_commerce.payment.exception.PayTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class TossExceptionTranslatorTest {
    TossExceptionTranslator tossExceptionTranslator = new TossExceptionTranslator(
            new TossErrorResponseHandler(new ObjectMapper()));

    @Test
    void read_timeout이면_PayNetworkException을_반환한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("Read timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveException payApproveException = tossExceptionTranslator.translate(ex);

        // then
        assertThat(payApproveException).isInstanceOf(PayTimeoutException.class);
    }

    @Test
    void http_error_응답이면_PayClientException을_반환한다() {
        // given
        String body = """
                {
                  "code": "INVALID_REJECT_CARD",
                  "message": "카드 정보 오류"
                }
                """;

        RestClientResponseException ex =
                new HttpClientErrorException(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        body.getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8   // 👈 이거 반드시 넣기
                );

        // when
        PayApproveException payApproveException = tossExceptionTranslator.translate(ex);

        // then
        assertThat(payApproveException).isInstanceOf(PayClientException.class);
        PayClientException payClientException = (PayClientException) payApproveException;
        assertThat(payClientException.getErrorCode()).isEqualTo(PayErrorCode.INVALID_CARD);
    }

}