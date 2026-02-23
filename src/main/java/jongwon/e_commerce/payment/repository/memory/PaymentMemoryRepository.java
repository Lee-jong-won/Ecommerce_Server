package jongwon.e_commerce.payment.repository.memory;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.repository.PaymentRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PaymentMemoryRepository implements PaymentRepository {

    private static Map<Long, Pay> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Pay save(Long fkOrderId,
                    String paymentId,
                    String orderId,
                    Long payAmount) {
        Pay pay = Pay.create(fkOrderId, paymentId, orderId, payAmount);
        pay.setPayId(++sequence);
        store.put(pay.getPayId(), pay);
        return pay;
    }

    @Override
    public Optional<Pay> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Pay> findByOrderId(String orderId) {
        Pay findPay = null;
        for(Pay pay : store.values()){
            if(pay.getOrderId().equals(orderId)) {
                findPay = pay;
                break;
            }
        }
        return Optional.ofNullable(findPay);
    }

    @Override
    public Optional<Pay> findByFkOrderId(Long fkOrderId) {
        Pay findPay = null;
        for(Pay pay : store.values()){
            if(pay.getFkOrderId().equals(fkOrderId)) {
                findPay = pay;
                break;
            }
        }
        return Optional.ofNullable(findPay);
    }

    @Override
    public void clearStore() {
        store.clear();
    }
}
