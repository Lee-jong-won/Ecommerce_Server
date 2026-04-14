package jongwon.e_commerce.payment.domain.approve.result.fail;

public class InvalidErrorResponse implements PayApproveFail{
    @Override
    public PayErrorCode errorCode() {
        return PayErrorCode.INVALID_ERROR_RESPONSE;
    }
}
