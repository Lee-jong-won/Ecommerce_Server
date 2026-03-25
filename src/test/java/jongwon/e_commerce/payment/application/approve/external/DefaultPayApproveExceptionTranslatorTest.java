package jongwon.e_commerce.payment.application.approve.external;

import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPayApproveExceptionTranslatorTest {
    private final DefaultPayApproveExceptionTranslator translator =
            new DefaultPayApproveExceptionTranslator();

    @Test
    void read_timeout이면_PayApproveTimeout을_반환한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("Read timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(PayApproveTimeout.class);
    }

    @Test
    void connect_timeout이면_TEMPORARY_ERROR를_반환한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("connect timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(PayApproveFail.class);

        PayApproveFail fail = (PayApproveFail) result;
        assertThat(fail.getErrorCode()).isEqualTo("CONNECTION_TIMEOUT");
    }

    @Test
    void http_error_응답이면_body에서_code와_message를_파싱한다() {
        // given
        String body = """
                {
                  "code": "INVALID_CARD",
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
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(PayApproveFail.class);

        PayApproveFail fail = (PayApproveFail) result;
        assertThat(fail.getErrorCode()).isEqualTo("INVALID_CARD");
        assertThat(fail.getMessage()).isEqualTo("카드 정보 오류");
    }

    @Test
    void http_error인데_body가_json이_아니면_UNKNOWN_ERROR를_반환한다() {
        // given
        String body = "not json";

        RestClientResponseException ex =
                new HttpClientErrorException(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        body.getBytes(),
                        null
                );

        // when
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(PayApproveFail.class);

        PayApproveFail fail = (PayApproveFail) result;
        assertThat(fail.getErrorCode()).isEqualTo("PARSING_FAIL");
        assertThat(fail.getMessage()).isEqualTo("결제 처리 중 오류가 발생했습니다.");
    }

    @Test
    void 알수없는_예외면_UNKNOWN_ERROR를_반환한다() {
        // given
        RestClientException ex = new RestClientException("unknown");

        // when
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(PayApproveFail.class);

        PayApproveFail fail = (PayApproveFail) result;
        assertThat(fail.getErrorCode()).isEqualTo("UNKNOWN_ERROR");
    }
}