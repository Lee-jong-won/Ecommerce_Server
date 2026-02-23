package jongwon.e_commerce.payment.repository.memory;

import jongwon.e_commerce.payment.domain.MobilePhonePayDetail;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.repository.MobilePhonePayDetailRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MobilePhonePayDetailMemoryRepository implements MobilePhonePayDetailRepository {
    private static Map<Long, MobilePhonePayDetail> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public MobilePhonePayDetail save(Long fkPayId, TossPaymentApproveResponse.MobilePhoneDto dto) {
        MobilePhonePayDetail mobilePhonePayDetail = MobilePhonePayDetail.create(fkPayId, dto);
        mobilePhonePayDetail.setId(++sequence);
        store.put(mobilePhonePayDetail.getId(), mobilePhonePayDetail);
        return mobilePhonePayDetail;
    }

    @Override
    public Optional<MobilePhonePayDetail> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
