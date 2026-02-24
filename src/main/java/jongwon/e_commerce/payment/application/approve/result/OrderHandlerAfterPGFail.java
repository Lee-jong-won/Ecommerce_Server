package jongwon.e_commerce.payment.application.approve.result;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderHandlerAfterPGFail {
    private final OrderRepository orderRepository;
    @Transactional
    public void onFail(String orderId){
        try{
            Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                    () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
            );
            order.markFailed();
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(orderId, e);
        }
    }
}
