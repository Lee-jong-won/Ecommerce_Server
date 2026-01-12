package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.infra.PaymentRepository;
import jongwon.e_commerce.payment.presentation.dto.PaymentApproveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class paymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public void paymentApprove(PaymentApproveRequest paymentApproveRequest){

        Optional<Order> optionalOrder = findOrderById(paymentApproveRequest.getOrderId());
        if(!optionalOrder.isPresent())
            throw new OrderNotExistException("결제 정보에 대응되는 주문 정보가 존재하지 않습니다.");

        if(!validateAmount(optionalOrder.get().getTotalAmount(), paymentApproveRequest.getAmount()))
            throw new InvalidAmountException("주문 내역의 금액과, 결제 금액이 일치하지 않습니다!");

        Pay payment = Pay.create(
                paymentApproveRequest.getOrderId(),
                paymentApproveRequest.getPayOrderId(),
                paymentApproveRequest.getOrderName(),
                paymentApproveRequest.getPaymentKey(),
                paymentApproveRequest.getAmount()
        );
        paymentRepository.save(payment);

        //외부 api 호출


        //결제 승인 성공

        //결제 승인 실패

        //타임 아웃
    }


    public boolean validateAmount(int orderAmount, int payAmount){
        return orderAmount == payAmount;
    }

    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
