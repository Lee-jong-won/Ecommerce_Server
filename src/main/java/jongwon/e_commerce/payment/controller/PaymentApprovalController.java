package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
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

    @PostMapping
    public ResponseEntity<PayApproveOutcomeResponse> payApprove(@RequestBody PayApproveAttempt attempt){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentApprovalService.approvePayment(attempt));
    }

}
