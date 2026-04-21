package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.domain.approve.outcome.fail.InvalidCard;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.JsonParsingError;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.UnknownErrorCode;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.UnknownRestClientError;
import jongwon.e_commerce.payment.domain.approve.outcome.unknown.ReadTimeout;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPayApproveExceptionTranslatorTest {
    private final DefaultPayApproveExceptionTranslator translator =
            new DefaultPayApproveExceptionTranslator(new ObjectMapper());

    @Test
    void read_timeout이면_PayApproveTimeout을_반환한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("Read timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(ReadTimeout.class);
    }

    @Test
    void connect_timeout이면_TEMPORARY_ERROR를_반환한다() {
        // given
        SocketTimeoutException cause = new SocketTimeoutException("connect timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(ConnectionTimeout.class);
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
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(InvalidCard.class);
    }

    @Test
    void 등록되지_않은_에러코드가_수신되면_UnknownErrorCode가_반환된다(){
        // given
        String body = """
                {
                  "code": "UNKNOWN_ERORR_CODE",
                  "message": "등록되지_않은_에러코드"
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
        assertThat(result).isInstanceOf(UnknownErrorCode.class);
    }
    @Test
    void connection_Request_timeout이_발생하면_PayApproveFail을_반환한다(){
        // given
        ConnectionRequestTimeoutException cause = new ConnectionRequestTimeoutException();
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);

        // when
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(ConnectionRequestTimeout.class);
    }

    @Test
    void 에러응답의_body가_json이_아니면_JsonParsingError를_반환한다() {
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
        assertThat(result).isInstanceOf(JsonParsingError.class);
    }

    @Test
    void 알수없는_RestClient_예외는_UnknownRestClientError를_반환한다() {
        // given
        RestClientException ex = new RestClientException("unknown");

        // when
        PayApproveOutcome result = translator.translate(ex);

        // then
        assertThat(result).isInstanceOf(UnknownRestClientError.class);
    }
}