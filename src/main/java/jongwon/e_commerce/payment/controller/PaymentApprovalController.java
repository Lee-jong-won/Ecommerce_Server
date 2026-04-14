package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.common.argumentResolver.LoginMember;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.controller.response.PayOutcomeHttpMapper;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@Builder
@RequiredArgsConstructor
public class PaymentApprovalController {

    private final PaymentApprovalService paymentApprovalService;
    @PostMapping
    public ResponseEntity<?> payApprove(@LoginMember Member member,
                                                      @RequestBody PayApproveAttempt attempt,
                                                      @RequestHeader String idempotencyKey){
        PayApproveOutcome outcome = paymentApprovalService.approvePayment(member, attempt, idempotencyKey);
        ResponseEntity<?> responseEntity = PayOutcomeHttpMapper.toResponse(outcome);
        return responseEntity;
    }

}
