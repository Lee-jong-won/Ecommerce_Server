package jongwon.e_commerce.external.http.policy;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

public interface HttpClientPolicy {

    void apply(HttpClientBuilder builder);

}
