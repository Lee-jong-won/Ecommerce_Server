package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.MobilePhonePayDetail;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;

import java.util.Optional;

public interface MobilePhonePayDetailRepository {
    MobilePhonePayDetail save(Long fkPayId, TossPaymentApproveResponse.MobilePhoneDto dto);
    Optional<MobilePhonePayDetail> findById(Long id);
}
