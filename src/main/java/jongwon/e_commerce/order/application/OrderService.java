package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.order.repository.jpa.entity.OrderEntity;
import jongwon.e_commerce.order.repository.jpa.entity.OrderItemEntity;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderEntity order(Member member, String orderName, List<OrderItemCreate> requests){
        //주문 - 상품 만들기
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        for(int i = 0; i < requests.size(); i++){
            OrderItemCreate orderItemCreate = requests.get(i);

            Product product = productRepository.findById(orderItemCreate.getProductId()).orElseThrow(
                    () -> new ProductNotFoundException(orderItemCreate.getProductId())
            );

            OrderItemEntity orderItemEntity = OrderItemEntity.createOrderItem(product,
                    product.getProductName(),
                    product.getProductPrice(),
                    orderItemCreate.getStockQuantity());

            orderItemEntities.add(orderItemEntity);
        }

        // 주문 생성
        OrderEntity orderEntity = OrderEntity.createOrder(member, orderName, LocalDateTime.now(), orderItemEntities);
        orderRepository.save(orderEntity);
        for(OrderItemEntity orderItemEntity : orderItemEntities) {
            orderItemEntity.setOrderEntity(orderEntity);
            orderItemRepository.save(orderItemEntity);
        }
        return orderEntity;
    }
}
