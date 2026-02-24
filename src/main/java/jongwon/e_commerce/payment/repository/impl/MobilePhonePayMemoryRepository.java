package jongwon.e_commerce.payment.repository.impl;

import jongwon.e_commerce.payment.domain.MobilePhonePay;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.repository.MobilePhonePayRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MobilePhonePayMemoryRepository implements MobilePhonePayRepository {
    private static Map<Long, MobilePhonePay> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public MobilePhonePay save(Long fkPayId, TossPaymentApproveResponse.MobilePhoneDto dto) {
        MobilePhonePay mobilePhonePay = MobilePhonePay.create(fkPayId, dto);
        mobilePhonePay.setId(++sequence);
        store.put(mobilePhonePay.getId(), mobilePhonePay);
        return mobilePhonePay;
    }

    @Override
    public Optional<MobilePhonePay> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
