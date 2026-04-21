package jongwon.e_commerce.payment.domain.approve.outcome.fail;

public class InsufficientBalance implements PayApproveFail {
    @Override
    public PayErrorCode errorCode() {
        return PayErrorCode.INSUFFICIENT_BALANCE;
    }
}
