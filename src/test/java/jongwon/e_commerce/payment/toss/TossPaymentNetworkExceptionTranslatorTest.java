package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class TossPaymentNetworkExceptionTranslatorTest {

    @Test
    void 타임아웃_예외가_토스예외로_정상적으로_해석된다(){
        //given
        ResourceAccessException resourceAccessException = new ResourceAccessException("타임아웃 예외",
                new SocketTimeoutException());

        //when
        TossPaymentNetworkExceptionTranslator tossPaymentNetworkExceptionTranslator = new TossPaymentNetworkExceptionTranslator();

        //then
        assertInstanceOf(TossPaymentTimeoutException.class, tossPaymentNetworkExceptionTranslator.translateNetworkException(resourceAccessException));
    }

    @Test
    void 타임아웃이_아닌_네트워크_예외가_토스예외로_정상적으로_해석된다(){

        //given
        ResourceAccessException resourceAccessException = new ResourceAccessException("연결 예외", new ConnectException());

        //when
        TossPaymentNetworkExceptionTranslator tossPaymentNetworkExceptionTranslator = new TossPaymentNetworkExceptionTranslator();

        //then
        assertInstanceOf(TossPaymentException.class, tossPaymentNetworkExceptionTranslator.translateNetworkException(resourceAccessException));
    }

}