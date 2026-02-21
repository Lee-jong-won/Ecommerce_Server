package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentNetworkException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

@Component
@Slf4j
public class TossPaymentNetworkExceptionTranslator {
    public TossPaymentException translateNetworkException(ResourceAccessException e) {
        Throwable cause = e.getCause();
        if (isReadTimeout(cause)) {
            log.error("[TOSS_API_READ_TIMEOUT]", e);
            return new TossPaymentTimeoutException("타임아웃 발생!");
        }

        log.error("[TOSS_API_NETWORK_ERROR]", e);
        return new TossPaymentNetworkException("네트워크 오류 발생!");
    }

    private boolean isReadTimeout(Throwable cause) {
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
