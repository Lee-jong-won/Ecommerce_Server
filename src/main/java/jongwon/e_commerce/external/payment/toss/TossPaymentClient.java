package jongwon.e_commerce.external.payment.toss;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TossPaymentClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(TossPaymentProperties properties, ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getApiUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        buildBasicAuthHeader(properties.getSecretKey(), properties.getBasicAuthPrefix())
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    private String buildBasicAuthHeader(String secretKey, String basicAuthPrefix){
        return basicAuthPrefix + " " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
