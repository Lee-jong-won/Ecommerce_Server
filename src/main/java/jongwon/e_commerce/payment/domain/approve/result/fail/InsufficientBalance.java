package jongwon.e_commerce.payment.domain.approve.result.fail;

public class InsufficientBalance implements PayApproveFail {
    @Override
    public PayErrorCode errorCode() {
        return PayErrorCode.INSUFFICIENT_BALANCE;
    }
}
