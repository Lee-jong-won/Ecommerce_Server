package jongwon.e_commerce.external.http.policy;

public record TimeoutPolicy(int connectTimeoutSeconds,
                            int responseTimeoutSeconds,
                            int connectionRequestTimeoutSeconds) {
}
