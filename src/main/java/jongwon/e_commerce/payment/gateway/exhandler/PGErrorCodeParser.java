package jongwon.e_commerce.payment.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;

public interface PGErrorCodeParser {

    PayApproveOutcome parse(String body);

}
