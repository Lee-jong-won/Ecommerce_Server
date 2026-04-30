package jongwon.e_commerce.payment.domain.approve.outcome.fail;

public class JsonParsingError implements PayApproveFail{
    @Override
    public PayErrorCode errorCode() {
        return PayErrorCode.JSON_PARSING_ERROR;
    }
}
