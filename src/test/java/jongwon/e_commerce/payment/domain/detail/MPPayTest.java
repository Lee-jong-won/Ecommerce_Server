package jongwon.e_commerce.payment.domain.detail;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MPPayTest {

    @Test
    void from_메서드로_MPPay가_정상적으로_생성된다() {
        // given
        String customerMobilePhone = "01012345678";
        String settlementStatus = "SETTLED";
        String receiptUrl = "http://receipt.url";

        // when
        MPPay mpPay = MPPay.from(customerMobilePhone, settlementStatus, receiptUrl);

        // then
        assertThat(mpPay.getCustomerMobilePhone()).isEqualTo(customerMobilePhone);
        assertThat(mpPay.getSettlementStatus()).isEqualTo(settlementStatus);
        assertThat(mpPay.getReceiptUrl()).isEqualTo(receiptUrl);
    }

}