package jongwon.e_commerce.payment.controller.response;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayResult;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InvalidCard;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.outcome.unknown.ReadTimeout;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PayOutcomeHttpMapperTest {

    @Test
    @DisplayName("성공 응답은 200 OK와 결제/주문 정보 반환")
    void successResponse(){
        // given
        OffsetDateTime approvedAt = OffsetDateTime.now();
        PayApproveOutcome outcome = new PayApproveSuccess(
                PayResult.builder().
                        payResultCommon(
                                PayResult.PayResultCommon.builder().
                                        payMethod(PayMethod.MOBILE).
                                        approvedAt(approvedAt).
                                        build()).
                        paymentDetail(
                                MPPay.builder().
                                        customerMobilePhone("010-1234-5678").
                                        settlementStatus("DONE").
                                        receiptUrl("naver").
                                        pay(Pay.builder().payAmount(1000).
                                                order(Order.builder().orderName("주문1").
                                                        totalAmount(1000).
                                                        build()).
                                                build()).
                                        build()
                        ).build());

        // when
        ResponseEntity<?> response = PayOutcomeHttpMapper.toResponse(outcome);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PayApproveSuccessResponse body = (PayApproveSuccessResponse) response.getBody();
        assertThat(body.getPayAmount()).isEqualTo(1000);
        assertThat(body.getOrderPrice()).isEqualTo(1000);
        assertThat(body.getOrderName()).isEqualTo("주문1");
    }

    @Test
    @DisplayName("카드 결제 실패는 400 반환")
    void 카드_결제_실패_Response(){
        // given
        InvalidCard invalidCard = new InvalidCard();

        // when
        ResponseEntity<?> response = PayOutcomeHttpMapper.toResponse(invalidCard);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        PayApproveFailResponse body = (PayApproveFailResponse) response.getBody();
        assertThat(body.getCode()).isEqualTo("INVALID_CARD");
    }

    @Test
    @DisplayName("ReadTimeout은 504 반환")
    void ConnectTimeoutResponse(){
        // given
        PayApproveOutcome outcome = new ReadTimeout();

        // when
        ResponseEntity<?> response = PayOutcomeHttpMapper.toResponse(outcome);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        PayApproveFailResponse body = (PayApproveFailResponse) response.getBody();
        assertThat(body.getCode()).isEqualTo("NETWORK_ERROR");
    }

    @Test
    @DisplayName("Connect Timeout은 504 반환")
    void networkTimeoutResponse() {
        // given
        PayApproveOutcome outcome = new ConnectionTimeout();

        // when
        ResponseEntity<?> response = PayOutcomeHttpMapper.toResponse(outcome);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        PayApproveFailResponse body = (PayApproveFailResponse) response.getBody();
        assertThat(body.getCode()).isEqualTo("NETWORK_ERROR");
    }

    @Test
    @DisplayName("RestClient 커넥션 풀 부족은 503 반환")
    void connectionPoolTimeoutResponse() {
        // given
        PayApproveOutcome outcome = new ConnectionRequestTimeout();

        // when
        ResponseEntity<?> response = PayOutcomeHttpMapper.toResponse(outcome);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        PayApproveFailResponse body = (PayApproveFailResponse) response.getBody();
        assertThat(body.getCode()).isEqualTo("NO_CONNECTION_TO_USE");
    }
}