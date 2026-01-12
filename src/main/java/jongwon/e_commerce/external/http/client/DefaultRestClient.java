package jongwon.e_commerce.external.http.client;

import jongwon.e_commerce.payment.infra.toss.TossPaymentProperties;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DefaultRestClient {
    private final RestClient restClient;

    public DefaultRestClient(
            HttpClientFactory factory,
            TossPaymentProperties properties
    ) {
        this.restClient = createRestClient(factory, properties);
    }

    private RestClient createRestClient(
            HttpClientFactory factory,
            TossPaymentProperties properties
    ){
        HttpClient httpClient = factory.create(null);

        return RestClient.builder()
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }
}
