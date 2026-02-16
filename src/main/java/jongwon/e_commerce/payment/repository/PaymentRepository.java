package jongwon.e_commerce.payment.repository;

import jongwon.e_commerce.payment.domain.Pay;

public interface PaymentRepository {
    Pay save(Long fkOrderId, String orderId, String pgType, Long payAmount);
    Pay findByOrderId(String orderId);
    Pay findByFkOrderId(Long fkOrderId);
}
