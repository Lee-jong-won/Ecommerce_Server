package jongwon.e_commerce.payment.application.approve.result.success.detailcreator;

import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.repository.MobilePhonePayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MobilePhonePayCreatorTest {
    @Mock
    MobilePhonePayRepository mobilePhonePayRepository;
    @InjectMocks
    MobilePhonePayCreator mobilePhonePayCreator;

    @Test
    void 정상적으로_핸드폰_상세_결제정보가_저장된다(){
        // given
        TossPaymentApproveResponse.MobilePhoneDto dto = new TossPaymentApproveResponse.MobilePhoneDto(
                "010-1234-5678", "완료", "http://naver.com");
        TossPaymentApproveResponse response = new TossPaymentApproveResponse(
                "paymentKey", "주문1", "카드",
                OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE", dto);

        // when
        mobilePhonePayCreator.save(1L, response);

        // then
        verify(mobilePhonePayRepository).save(1L, dto);
    }
}