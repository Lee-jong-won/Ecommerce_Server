package jongwon.e_commerce.payment.application.approve.settlement.handler.success;


import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.repository.jpa.MPPayJpaRepository;
import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import jongwon.e_commerce.payment.repository.jpa.entity.PayDetailEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PayDetailSaver {

    private final MPPayJpaRepository mpPayJpaRepository;

    public PaymentDetail save(PaymentDetail paymentDetail) {
        PayDetailEntity payDetailEntity = paymentDetail.toEntity();
        switch(paymentDetail.getPayMethod()){
            case MOBILE -> mpPayJpaRepository.save((MPPayEntity) payDetailEntity);
        }
        return payDetailEntity.toModel();
    }
}
