package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class PaymentInquiryClientImpl implements PaymentInquiryClient{

    @Qualifier("tossRestClient")private final RestClient restClient;

    @Override
    public TossPaymentApproveResponse callPayInquiryApi(String paymentKey) {
        return restClient.get()
                .uri("/payments/{paymentKey}", paymentKey).retrieve()
                .body(TossPaymentApproveResponse.class);
    }
}
