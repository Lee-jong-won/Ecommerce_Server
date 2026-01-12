package jongwon.e_commerce.external.http.policy;

public record ConnectionPoolPolicy(int maxTotalConnections,
                                   int maxConnectionsPerRoute,
                                   int idleConnectionSeconds) {
}
