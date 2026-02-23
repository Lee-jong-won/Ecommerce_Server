package jongwon.e_commerce.payment.application.approve.process3.success.detailcreator;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayMethodMapper;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.PaymentNotFoundException;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentDetailCreatorService {

    private final List<PaymentDetailCreator> creators;
    private final PaymentRepository paymentRepository;

    public void saveDetail(TossPaymentApproveResponse response) {
        Pay pay = paymentRepository.findByOrderId(response.getOrderId()).orElseThrow(
                () ->  new PaymentNotFoundException("주문 정보에 대응되는 결제 정보를 찾을 수 없습니다")
        );

        PayMethod method = PayMethodMapper.from(response.getMethod());
        PaymentDetailCreator creator = creators.stream()
                .filter(h -> h.supports() == method)
                .findFirst()
                .orElseThrow(() ->
                        new UnsupportedPayMethodException("지원하지 않는 결제 수단"));

        creator.save(pay.getPayId(), response);
    }

}
