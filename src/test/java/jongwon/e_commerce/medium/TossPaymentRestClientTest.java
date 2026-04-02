package jongwon.e_commerce.medium;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TossPaymentRestClientTest {

    @Autowired
    RestClient restClient;

    @Test
    void 클라우드에_있는_인스턴스로부터_정상_응답이_돌아온다(){
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        TossPaymentApproveResponse response = restClient.post()
                .uri("/payments/confirm")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .body(request)
                .retrieve()
                .body(TossPaymentApproveResponse.class);
        TossPaymentApproveResponse.MobilePhoneDto mobilePhoneDto = response.getMobilePhone();

        // then
        assertThat(response.getApprovedAt()).isEqualTo(OffsetDateTime.parse("2026-04-02T12:00:00+09:00"));
        assertThat(response.getMethod()).isEqualTo("휴대폰");
        assertThat(mobilePhoneDto.getCustomerMobilePhone()).isEqualTo("01012345678");
        assertThat(mobilePhoneDto.getSettlementStatus()).isEqualTo("SETTLED");
        assertThat(mobilePhoneDto.getReceiptUrl()).isEqualTo("http://receipt.url");
    }


}
