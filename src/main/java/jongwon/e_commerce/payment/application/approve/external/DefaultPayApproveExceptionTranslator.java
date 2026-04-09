package jongwon.e_commerce.payment.application.approve.external;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.SocketTimeoutException;

@Component
@Slf4j
public class DefaultPayApproveExceptionTranslator implements PayApproveExceptionTranslator {
    @Override
    public PayApproveOutcome translate(RestClientException restClientException) {
        // Read 타임아웃
        if(restClientException instanceof ResourceAccessException){
            if(isReadTimeout(restClientException.getCause()))
                return new PayApproveTimeout();

            if(isConnectTimeout(restClientException.getCause()))
                return new PayApproveFail(PayErrorCode.CONNECTION_TIMEOUT,
                    "일시적인 네트워크 오류가 발생했습니다. 다시 시도해주세요");

            if(isConnectionRequestTimeout(restClientException.getCause()))
                return new PayApproveFail(PayErrorCode.HTTP_CLIENT_POOL_TIMEOUT, "요청이 너무 많습니다");
        }

        // Http 에러 응답
        if(restClientException instanceof RestClientResponseException)
            return translateFromResponse((RestClientResponseException) restClientException);

        log.error("RestClientException occured!", restClientException);
        // 알 수 없는 예외
        return new PayApproveFail(
                PayErrorCode.UNKNOWN,
                "알 수 없는 오류가 발생했습니다"
        );
    }

    private boolean isConnectionRequestTimeout(Throwable cause){
        while (cause != null) {
            if (cause instanceof ConnectionRequestTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private boolean isReadTimeout(Throwable cause) {
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                String msg = cause.getMessage();
                if (msg != null && msg.toLowerCase().contains("read timed out")) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    private boolean isConnectTimeout(Throwable cause) {
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                String msg = cause.getMessage();
                if (msg != null && msg.toLowerCase().contains("connect timed out")) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    private PayApproveOutcome translateFromResponse(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(body);

            String code = json.path("code").asText("UNKNOWN_ERROR");
            String message = json.path("message").asText("결제 처리 중 오류가 발생했습니다.");
            return new PayApproveFail(PayErrorCode.from(code), message);

        } catch (Exception e) {
            // JSON 파싱 실패 시 fallback
            return new PayApproveFail(
                    PayErrorCode.UNKNOWN,
                    "결제 처리 중 오류가 발생했습니다."
            );
        }
    }

}
