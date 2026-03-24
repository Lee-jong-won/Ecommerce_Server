package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.PaymentApproveClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;

public class StubPaymentRestApproveClientErrorResponse implements PaymentApproveClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        String body = """
        {
            "code" : "FAILED_INTERNAL_SYSTEM_PROCESSING",
            "message" : "내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요."
        }
        """;

        RestClientResponseException exception =
                new RestClientResponseException(
                        "Internal Server Error",   // message
                        500,                      // rawStatusCode
                        "Internal Server Error",  // statusText
                        HttpHeaders.EMPTY,        // headers
                        body.getBytes(StandardCharsets.UTF_8), // response body
                        StandardCharsets.UTF_8    // charset
                );

        throw exception;
    }

}
