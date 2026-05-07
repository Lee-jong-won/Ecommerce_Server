package jongwon.e_commerce.payment.infrastructure.gateway.exhandler;

import jongwon.e_commerce.payment.exception.ExceptionUtils;
import jongwon.e_commerce.payment.exception.PayApproveException;
import jongwon.e_commerce.payment.exception.PayTimeoutException;
import jongwon.e_commerce.payment.exception.PayServerException;
import org.springframework.web.client.ResourceAccessException;

public class IOExceptionHandler {
    public static PayApproveException handle(ResourceAccessException e) {
        Throwable cause = e.getCause();

        if (ExceptionUtils.isReadTimeout(cause)) {
            return new PayTimeoutException();
        }
        if (ExceptionUtils.isConnectTimeout(cause)) {
            return new PayServerException("연결 타임아웃이 발생했습니다.");
        }
        if (ExceptionUtils.isConnectionRequestTimeout(cause)) {
            return new PayServerException("커넥션 풀이 고갈되었습니다.");
        }

        return new PayServerException("알 수 없는 연결 오류가 발생했습니다.");
    }
}
