package jongwon.e_commerce.external.http.policy;

import lombok.Builder;
import lombok.Getter;
import org.apache.hc.core5.util.Timeout;

@Getter
@Builder
public class RequestConfigPolicy {
    /**
     * 서버 응답 대기 타임아웃 (seconds)
     */
    private Timeout responseTimeoutSeconds;

    /**
     * 커넥션 풀에서 커넥션을 얻기까지의 타임아웃 (seconds)
     */
    private Timeout connectionRequestTimeoutSeconds;

}
