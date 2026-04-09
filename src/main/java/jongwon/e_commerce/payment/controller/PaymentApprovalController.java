package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.common.argumentResolver.LoginMember;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.controller.dto.PayApproveOutcomeResponse;
import jongwon.e_commerce.payment.controller.dto.body.PayResponseBody;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcomeType;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveSuccess;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@Builder
@RequiredArgsConstructor
public class PaymentApprovalController {

    private final PaymentApprovalService paymentApprovalService;
    private final PayFailureResponseMapper payFailureResponseMapper;
    @PostMapping
    public ResponseEntity<PayResponseBody> payApprove(@LoginMember Member member,
                                                      @RequestBody PayApproveAttempt attempt){
        PayApproveOutcome outcome = paymentApprovalService.approvePayment(member, attempt);

        if(outcome.getType() == PayApproveOutcomeType.SUCCESS){
            PayApproveSuccess payApproveSuccess = (PayApproveSuccess) outcome;
            return ResponseEntity.status(HttpStatus.CREATED).body()
        }

        if(outcome.getType() == PayApproveOutcomeType.FAIL){

        }


    }

}
