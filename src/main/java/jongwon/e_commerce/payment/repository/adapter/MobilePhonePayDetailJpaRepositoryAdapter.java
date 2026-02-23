package jongwon.e_commerce.payment.repository.adapter;

import jongwon.e_commerce.payment.domain.MobilePhonePayDetail;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.repository.MobilePhonePayDetailRepository;
import jongwon.e_commerce.payment.repository.jpa.MobilePhonePayDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class MobilePhonePayDetailJpaRepositoryAdapter implements MobilePhonePayDetailRepository {

    private final MobilePhonePayDetailJpaRepository mobilePhonePayDetailJpaRepository;

    @Override
    public MobilePhonePayDetail save(Long fkPayId, TossPaymentApproveResponse.MobilePhoneDto dto) {
        MobilePhonePayDetail mobilePhonePayDetail = MobilePhonePayDetail.create(fkPayId, dto);
        mobilePhonePayDetailJpaRepository.save(mobilePhonePayDetail);
        return mobilePhonePayDetail;
    }

    @Override
    public Optional<MobilePhonePayDetail> findById(Long id) {
        return mobilePhonePayDetailJpaRepository.findById(id);
    }
}
