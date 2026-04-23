package jongwon.e_commerce.payment.application.approve.handler.success;


import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import jongwon.e_commerce.payment.repository.MPPayRepository;
import jongwon.e_commerce.payment.repository.jpa.MPPayJpaRepository;
import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import jongwon.e_commerce.payment.repository.jpa.entity.PayDetailEntity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Builder
public class PayDetailSaver {

    private final MPPayRepository mpPayRepository;

    @Transactional
    public PaymentDetail save(Pay pay, PaymentDetail paymentDetail) {
        paymentDetail.setPay(pay);
        return switch (paymentDetail.getPayMethod()) {
            case MOBILE -> mpPayRepository.save((MPPay) paymentDetail);
            default -> throw new UnsupportedPayMethodException();
        };
    }
}
