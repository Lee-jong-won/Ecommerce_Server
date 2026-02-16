package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.exception.external.TossPaymentClientException;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TossPaymentClientErrorHandler implements RestClient

        .ResponseSpec.ErrorHandler {
    @Override
    public void handle(
            HttpRequest request,
            ClientHttpResponse response
    ) {
        try {
            if(response.getStatusCode().is4xxClientError()){
                throw new TossPaymentClientException("결제 중 오류가 발생했습니다. 다시 시도해주세요");
            }

            if(response.getStatusCode().is5xxServerError()){
                throw new TossPaymentServerException("결제 중 오류가 발생했습니다. 다시 시도해주세요");
            }
        } catch (IOException e) {
            throw new TossPaymentException("알 수 없는 오류입니다.");
        }
    }
}
