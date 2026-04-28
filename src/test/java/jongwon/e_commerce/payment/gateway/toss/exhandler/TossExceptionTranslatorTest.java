package jongwon.e_commerce.payment.gateway.toss.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InvalidCard;
import jongwon.e_commerce.payment.domain.approve.outcome.unknown.ReadTimeout;
import jongwon.e_commerce.payment.gateway.exhandler.NetworkExceptionHandler;
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
            new NetworkExceptionHandler(), new TossErrorResponseHandler(new ObjectMapper()));

    @Test
    void read_timeout이면_PayApproveTimeout을_반환한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("Read timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = tossExceptionTranslator.translate(ex);

        // then
        assertThat(result).isInstanceOf(ReadTimeout.class);
    }

    @Test
    void http_error_응답이면_body에서_code와_message를_파싱한다() {
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
        PayApproveOutcome result = tossExceptionTranslator.translate(ex);

        // then
        assertThat(result).isInstanceOf(InvalidCard.class);
    }

}