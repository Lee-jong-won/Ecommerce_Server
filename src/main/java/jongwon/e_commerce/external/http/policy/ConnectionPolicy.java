package jongwon.e_commerce.external.http.policy;

import lombok.Builder;
import lombok.Getter;
import org.apache.hc.core5.util.Timeout;

@Builder
@Getter
public class ConnectionPolicy {
    /**
     * 전체 최대 커넥션 수
     */
    private Integer maxTotalConnections;
    /**
     * 호스트(라우트)별 최대 커넥션 수
     */
    private Integer maxConnectionsPerRoute;
    /**
     * 커넥션 타임아웃 시간 - 3 way handshake
     */
    private Timeout connectTimeout;
    /**
     * Readtimeout 시간
     */
    private Timeout socketTimeout;

}
