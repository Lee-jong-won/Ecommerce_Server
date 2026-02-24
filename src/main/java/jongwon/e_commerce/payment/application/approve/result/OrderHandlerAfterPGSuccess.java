package jongwon.e_commerce.payment.application.approve.result;

import jongwon.e_commerce.order.application.StockChangerByOrder;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentContext;
import jongwon.e_commerce.payment.application.approve.result.persistence.PaymentCreator;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderHandlerAfterPGSuccess {

    private final StockChangerByOrder stockChangerByOrder;
    private final OrderRepository orderRepository;
    private final PaymentCreator paymentCreator;

    @Transactional
    public void onSuccess(String orderId, PaymentContext paymentContext){
        try{
            //1. 주문 상태 결제됨으로 변경
            Order order = orderRepository.findByOrderId(orderId).orElseThrow(
                    () -> new OrderNotExistException("해당 주문 ID를 갖는 주문 정보가 존재하지 않습니다.")
            );
            order.markPaid();

            //2. 재고 감소
            stockChangerByOrder.decreaseStockBy(order);

            //3. 결제 데이터 생성
            paymentCreator.createPayment(paymentContext);
        }catch(DataAccessException e){
            // Exception Handler에서 예외 처리
            throw new PaymentCompletionConsistencyException(paymentContext.getOrderId(), e);
        }
    }
}
