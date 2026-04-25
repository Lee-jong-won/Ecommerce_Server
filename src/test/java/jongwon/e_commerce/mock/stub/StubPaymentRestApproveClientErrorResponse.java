package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.gateway.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.PaymentApproveClient;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;

public class StubPaymentRestApproveClientErrorResponse implements PaymentApproveClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request) {
        String body = """
        {
            "code" : "INVALID_REJECT_CARD",
            "message" : "잘못된 카드 정보입니다."
        }
        """;

        RestClientResponseException exception =
                new RestClientResponseException(
                        "BAD_REQUEST",   // message
                        400,                      // rawStatusCode
                        "BAD_REQUEST",  // statusText
                        HttpHeaders.EMPTY,        // headers
                        body.getBytes(StandardCharsets.UTF_8), // response body
                        StandardCharsets.UTF_8    // charset
                );

        throw exception;
    }

}
