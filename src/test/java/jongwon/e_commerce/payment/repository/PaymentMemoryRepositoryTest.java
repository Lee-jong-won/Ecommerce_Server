package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentMemoryRepositoryTest {

    PaymentMemoryRepository paymentMemoryRepository = new PaymentMemoryRepository();

    @AfterEach
    public void afterEach(){
        paymentMemoryRepository.clearStore();
    }

    @Test
    void Payment_저장_테스트(){
        // given
        Long fkOrderId = 1L;
        String orderId = "order1";
        Long payAmount = 10000L;

        // when
        Pay pay = paymentMemoryRepository.save(fkOrderId, orderId, payAmount);

        // then
        Pay result = paymentMemoryRepository.findById(pay.getPayId()).get();
        assertThat(result).isEqualTo(pay);
    }

    @Test
    void Payment를_orderId로_조회_테스트_정상(){
        // given
        Pay pay1= paymentMemoryRepository.save(1L, "order1", 10000L);
        paymentMemoryRepository.save(1L, "order2", 10000L);

        // when
        Pay result = paymentMemoryRepository.findByOrderId("order1").get();

        // then
        assertThat(result).isEqualTo(pay1);
    }

    @Test
    void Payment를_fkOrderId로_조회_테스트_정상(){
        // given
        Pay pay1= paymentMemoryRepository.save(1L, "order1", 10000L);
        paymentMemoryRepository.save(2L, "order2", 10000L);

        // when
        Pay result = paymentMemoryRepository.findByFkOrderId(1L).get();

        // then
        assertThat(result).isEqualTo(pay1);
    }
}