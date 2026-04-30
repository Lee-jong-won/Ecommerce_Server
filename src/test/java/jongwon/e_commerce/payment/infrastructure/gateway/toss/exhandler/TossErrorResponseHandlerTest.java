package jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InvalidCard;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.JsonParsingError;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.UnknownErrorCode;
import jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler.TossErrorResponseHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class TossErrorResponseHandlerTest {

    TossErrorResponseHandler tossErrorResponseHandler = new TossErrorResponseHandler(new ObjectMapper());

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
        PayApproveOutcome result = tossErrorResponseHandler.handle(ex);

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
                        StandardCharsets.UTF_8
                );

        // when
        PayApproveOutcome result = tossErrorResponseHandler.handle(ex);

        // then
        assertThat(result).isInstanceOf(UnknownErrorCode.class);
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
        PayApproveOutcome result = tossErrorResponseHandler.handle(ex);

        // then
        assertThat(result).isInstanceOf(JsonParsingError.class);
    }


}