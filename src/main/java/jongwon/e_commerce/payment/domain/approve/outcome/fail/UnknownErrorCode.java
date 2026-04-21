package jongwon.e_commerce.payment.domain.approve.outcome.fail;

public class UnknownErrorCode implements PayApproveFail{
    @Override
    public PayErrorCode errorCode() {
        return PayErrorCode.UNKNOWN_ERROR_CODE;
    }
}
