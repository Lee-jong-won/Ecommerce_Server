package jongwon.e_commerce.external.http.policy;

public record RetryPolicy(int maxRetries,
                          int retryIntervalSeconds) {
}
