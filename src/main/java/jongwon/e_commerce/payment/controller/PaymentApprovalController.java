package jongwon.e_commerce.payment.controller;

import jongwon.e_commerce.common.argumentResolver.LoginMember;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.controller.response.MessageResolver;
import jongwon.e_commerce.payment.controller.response.PayApproveFailResponse;
import jongwon.e_commerce.payment.controller.response.PayApproveSuccessResponse;
import jongwon.e_commerce.payment.exception.PayClientException;
import jongwon.e_commerce.payment.exception.PayGatewayException;
import jongwon.e_commerce.payment.exception.PayServerException;
import jongwon.e_commerce.payment.exception.PayUnknownOutcomeException;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentApprovalController {

    private final PaymentApprovalService paymentApprovalService;
    @PostMapping
    public ResponseEntity<PayApproveSuccessResponse> payApprove(@LoginMember Member member,
                                                             @RequestBody PayApproveAttempt attempt) {
        PayApproveSuccessResponse responseBody = PayApproveSuccessResponse.from(
                paymentApprovalService.approvePayment(member, attempt));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @ExceptionHandler(PayClientException.class)
    public ResponseEntity<PayApproveFailResponse> handle(PayClientException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(PayApproveFailResponse.builder()
                        .code(ex.getErrorCode().name())
                        .message(MessageResolver.resolve(ex.getErrorCode()))
                        .build());
    }

    @ExceptionHandler(PayGatewayException.class)
    public ResponseEntity<PayApproveFailResponse> handle(PayGatewayException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(PayApproveFailResponse.builder()
                        .code("GATEWAY_ERROR")
                        .message("PG사 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
                        .build());
    }

    @ExceptionHandler(PayUnknownOutcomeException.class)
    public ResponseEntity<PayApproveFailResponse> handle(PayUnknownOutcomeException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(PayApproveFailResponse.builder()
                        .code("NETWORK_ERROR")
                        .message("일시적인 네트워크 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
                        .build());
    }

    @ExceptionHandler(PayServerException.class)
    public ResponseEntity<PayApproveFailResponse> handle(PayServerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PayApproveFailResponse.builder()
                        .code("SERVER_ERROR")
                        .message("일시적인 서버 오류입니다. 잠시 후 다시 시도해 주세요.")
                        .build());
    }
}
