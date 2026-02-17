package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;

class TossNetworkExceptionTranslatorTest {

    @Test
    void 타임아웃_예외가_토스예외로_정상적으로_해석된다(){
        //given
        ResourceAccessException resourceAccessException = new ResourceAccessException("타임아웃 예외",
                new SocketTimeoutException());

        //when
        TossNetworkExceptionTranslator tossNetworkExceptionTranslator = new TossNetworkExceptionTranslator();

        //then
        assertInstanceOf(TossPaymentTimeoutException.class, tossNetworkExceptionTranslator.translateNetworkException(resourceAccessException));
    }

    @Test
    void 타임아웃이_아닌_네트워크_예외가_토스예외로_정상적으로_해석된다(){

        //given
        ResourceAccessException resourceAccessException = new ResourceAccessException("연결 예외", new ConnectException());

        //when
        TossNetworkExceptionTranslator tossNetworkExceptionTranslator = new TossNetworkExceptionTranslator();

        //then
        assertInstanceOf(TossPaymentException.class, tossNetworkExceptionTranslator.translateNetworkException(resourceAccessException));
    }

}