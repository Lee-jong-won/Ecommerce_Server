package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentSystemException.TossPaymentSystemException;
import jongwon.e_commerce.payment.presentation.dto.TossErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class TossPaymentClientErrorHandler implements RestClient

        .ResponseSpec.ErrorHandler {

    private final ObjectMapper objectMapper;
    private final TossPaymentErrorMapper errorMapper;

    @Override
    public void handle(
            HttpRequest request,
            ClientHttpResponse response
    ) throws IOException {
        String body;
        try {
            body = StreamUtils.copyToString(
                    response.getBody(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            log.error("[TOSS_ERROR_HANDLER_BODY_READ_FAIL]", e);
            throw new TossPaymentSystemException(
                    PaymentErrorCode.UNKNOWN_ERROR
            );
        }

        TossErrorResponse error;
        try {
            error = parse(body);
        } catch (Exception e) {
            log.error("[TOSS_ERROR_HANDLER_PARSE_FAIL] body={}", body, e);
            throw new TossPaymentSystemException(
                    PaymentErrorCode.UNKNOWN_ERROR
            );
        }

        TossPaymentException ex = errorMapper.map(error.getCode());
        if (ex instanceof TossPaymentSystemException) {
            log.error(
                    "[TOSS_API_ERROR] code={}, message={}",
                    error.getCode(),
                    error.getMessage()
            );
        }
        throw ex;
    }

    private TossErrorResponse parse(String body) {
        try {
            return objectMapper.readValue(body, TossErrorResponse.class);
        } catch (Exception e) {
            return new TossErrorResponse(
                    "UNKNOWN_ERROR",
                    "결제 처리 중 알 수 없는 오류가 발생했습니다."
            );
        }
    }
}
