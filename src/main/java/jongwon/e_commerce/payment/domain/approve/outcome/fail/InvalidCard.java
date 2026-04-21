package jongwon.e_commerce.payment.domain.approve.outcome.fail;

public class InvalidCard implements PayApproveFail{
    @Override
    public PayErrorCode errorCode() {
        return PayErrorCode.INVALID_CARD;
    }
}
