package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class PaymentSimulationController {

    @Autowired
    private final RestClient restClient;

    @PostMapping("/test/payment/confirm")
    public ResponseEntity<TossPaymentApproveResponse> testPaymentConfirm(@RequestBody PayApproveAttempt attempt){
        TossPaymentApproveResponse response = restClient.post()
                .uri("/payments/confirm")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .body(attempt)
                .retrieve()
                .body(TossPaymentApproveResponse.class);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
