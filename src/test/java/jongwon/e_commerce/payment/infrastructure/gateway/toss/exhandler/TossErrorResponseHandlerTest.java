package jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler;

import jongwon.e_commerce.payment.exception.*;
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
    void CODE와_MESSAGE를_해석해서_적절한_예외로_변환한다() {
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
        PayApproveException payApproveException = tossErrorResponseHandler.handle(ex);

        // then
        assertThat(payApproveException).isInstanceOf(PayClientException.class);
        PayClientException payClientException = (PayClientException) payApproveException;
        assertThat(payClientException.getErrorCode()).isEqualTo(PayErrorCode.INVALID_CARD);
    }

    @Test
    void 등록되지_않은_에러코드가_수신되면_PG예외가_반환된다(){
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
        PayApproveException payApproveException = tossErrorResponseHandler.handle(ex);

        // then
        assertThat(payApproveException).isInstanceOf(PayErrorResponseParsingException.class);
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
        PayApproveException payApproveException = tossErrorResponseHandler.handle(ex);

        // then
        assertThat(payApproveException).isInstanceOf(PayErrorResponseParsingException.class);
    }
}