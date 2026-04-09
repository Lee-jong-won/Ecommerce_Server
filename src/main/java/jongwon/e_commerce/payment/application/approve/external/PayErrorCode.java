package jongwon.e_commerce.payment.application.approve.external;

public enum PayErrorCode {
    CONNECTION_TIMEOUT(false),
    HTTP_CLIENT_POOL_TIMEOUT(false),
    DB_POOL_TIMEOUT(false),
    UNKNOWN(false),
    INVALID_CARD(true);
    private final boolean payFail;
    PayErrorCode(boolean payFail) {
        this.payFail = payFail;
    }

    public static PayErrorCode from(String code) {
        try {
            return PayErrorCode.valueOf(code);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    public boolean shouldPayFail(){
        return payFail;
    }

}
