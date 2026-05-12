package jongwon.e_commerce.mock.stub;


import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;

public class StubPaymentApprovalService {

    public PayResult returnPayApproveFail(){
        return null;
    }
}
