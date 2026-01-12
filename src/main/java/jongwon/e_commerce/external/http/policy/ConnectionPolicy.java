package jongwon.e_commerce.external.http.policy;

import lombok.Builder;
import lombok.Getter;

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
     * 호스트(라우트)별 최대 커넥션 수
     */
}
