package jongwon.e_commerce.UnitTest.payment.exception;

import jongwon.e_commerce.payment.exception.TossPaymentNotFoundException;
import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentAlreadyProcessedException;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossPaymentRetryableException;
import jongwon.e_commerce.payment.exception.external.TossPaymentSystemException.TossPaymentSystemException;
import jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException.TossPaymentUserFaultException;
import jongwon.e_commerce.payment.infra.toss.TossPaymentErrorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TossPaymentErrorMapperTest {

    private TossPaymentErrorMapper mapper;

    @BeforeEach
    void SetUp(){
        mapper = new TossPaymentErrorMapper();
    }

    @Test
    @DisplayName("INVALID_REQUEST는 UserFaultException으로 매핑된다")
    void invalidRequest_mapsToUserFaultException() {
        // when
        TossPaymentException ex = mapper.map("INVALID_REQUEST");

        // then
        assertThat(ex)
                .isInstanceOf(TossPaymentUserFaultException.class);

        assertThat(ex.getErrorCode())
                .isEqualTo(PaymentErrorCode.INVALID_REQUEST);
    }

    @Test
    @DisplayName("PROVIDER_ERROR는 RetryableException으로 매핑된다")
    void providerError_mapsToRetryableException() {
        TossPaymentException ex = mapper.map("PROVIDER_ERROR");

        assertThat(ex)
                .isInstanceOf(TossPaymentRetryableException.class);

        assertThat(ex.getErrorCode())
                .isEqualTo(PaymentErrorCode.PROVIDER_ERROR);
    }

    @Test
    @DisplayName("NOT_FOUND_PAYMENT는 NotFoundException으로 매핑된다")
    void notFoundPayment_mapsToNotFoundException() {
        TossPaymentException ex = mapper.map("NOT_FOUND_PAYMENT");

        assertThat(ex)
                .isInstanceOf(TossPaymentNotFoundException.class);

        assertThat(ex.getErrorCode())
                .isEqualTo(PaymentErrorCode.NOT_FOUND_PAYMENT);
    }

    @Test
    @DisplayName("INVALID_API_KEY는 SystemException으로 매핑된다")
    void invalidApiKey_mapsToSystemException() {
        TossPaymentException ex = mapper.map("INVALID_API_KEY");

        assertThat(ex)
                .isInstanceOf(TossPaymentSystemException.class);

        assertThat(ex.getErrorCode())
                .isEqualTo(PaymentErrorCode.INVALID_API_KEY);
    }

    @Test
    @DisplayName("ALREADY_PROCESSED_PAYMENT는 AlreadyProcessedException이다")
    void alreadyProcessedPayment_mapsToAlreadyProcessedException() {
        TossPaymentException ex = mapper.map("ALREADY_PROCESSED_PAYMENT");

        assertThat(ex)
                .isInstanceOf(TossPaymentAlreadyProcessedException.class);

        assertThat(ex.getErrorCode())
                .isEqualTo(PaymentErrorCode.ALREADY_PROCESSED_PAYMENT);
    }

    @Test
    @DisplayName("알 수 없는 코드는 UNKNOWN_ERROR로 매핑된다")
    void unknownCode_mapsToUnknownPaymentError() {
        TossPaymentException ex = mapper.map("THIS_CODE_DOES_NOT_EXIST");

        assertThat(ex)
                .isInstanceOf(TossPaymentException.class);

        assertThat(ex.getErrorCode())
                .isEqualTo(PaymentErrorCode.UNKNOWN_ERROR);
    }

}