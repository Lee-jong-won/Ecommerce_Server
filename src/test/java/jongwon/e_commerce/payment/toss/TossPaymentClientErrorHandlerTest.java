package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.exception.external.TossPaymentClientException;
import jongwon.e_commerce.payment.exception.external.TossPaymentServerException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TossPaymentClientErrorHandlerTest {

    @Test
    void 사백번대_에러가_TossClientException으로_해석된다(){
        //given
        HttpRequest httpRequest = new MockClientHttpRequest();
        ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(new byte[1], HttpStatusCode.valueOf(400));
        TossPaymentClientErrorHandler tossPaymentClientErrorHandler= new TossPaymentClientErrorHandler();

        //when && then
        assertThrows(TossPaymentClientException.class,
                () ->tossPaymentClientErrorHandler.handle(httpRequest, clientHttpResponse));
    }

    @Test
    void 오백번대_에러가_TossServerException으로_해석된다(){
        //given
        HttpRequest httpRequest = new MockClientHttpRequest();
        ClientHttpResponse clientHttpResponse = new MockClientHttpResponse(new byte[1], HttpStatusCode.valueOf(500));
        TossPaymentClientErrorHandler tossPaymentClientErrorHandler= new TossPaymentClientErrorHandler();

        //when && then
        assertThrows(TossPaymentServerException.class,
                () ->tossPaymentClientErrorHandler.handle(httpRequest, clientHttpResponse));
    }

}