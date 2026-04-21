package jongwon.e_commerce.support.scenario;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayResult;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.fixture.*;

import java.time.OffsetDateTime;
import java.util.List;

public class TestDataFactory {

    // 영속화 메소드
    public static PrepareOrderData prepareOrder(MemberRepository memberRepository,
                                                ProductRepository productRepository){
        Member member = memberRepository.save(MemberFixture.builder().build().create());
        Product product1 = productRepository.save(ProductFixture.
                builder().
                productName("상품1").
                productStatus(ProductStatus.SELLING).
                productPrice(10000).
                build().
                create());
        Product product2 = productRepository.save(ProductFixture.
                builder().
                productName("상품2").
                productStatus(ProductStatus.SELLING).
                productPrice(5000).
                build().
                create());
        OrderItemCreate orderItemCreate1 =  OrderItemCreate.builder().
                productId(product1.getProductId()).
                stockQuantity(1).
                build();
        OrderItemCreate orderItemCreate2 = OrderItemCreate.builder().
                productId(product2.getProductId()).
                stockQuantity(1).
                build();
        return new PrepareOrderData(member, List.of(orderItemCreate1, orderItemCreate2));
    }

    public static FinishOrderData finishOrder(MemberRepository memberRepository,
                                    ProductRepository productRepository,
                                    OrderItemRepository orderItemRepository,
                                    OrderRepository orderRepository){
        Member member = memberRepository.save(MemberFixture.builder().build().create());
        Product product1 = productRepository.save(ProductFixture.
                builder().
                productName("상품1").
                productStatus(ProductStatus.SELLING).
                productPrice(10000).
                build().
                create());
        Product product2 = productRepository.save(ProductFixture.
                builder().
                productName("상품2").
                productStatus(ProductStatus.SELLING).
                productPrice(5000).
                build().
                create());
        Order order = orderRepository.save(OrderFixture.
                builder().
                member(member).
                totalAmount(15000).
                build().
                create());
        OrderItem orderItem1 = orderItemRepository.save(OrderItemFixture.
                builder().
                order(order).
                product(product1).
                build().
                create());
        OrderItem orderItem2 = orderItemRepository.save(OrderItemFixture.
                builder().
                order(order).
                product(product2).
                build().create());
        return new FinishOrderData(member, order);
    }

    public static Pay finishPayPreProcess(MemberRepository memberRepository,
                                ProductRepository productRepository,
                                OrderItemRepository orderItemRepository,
                                OrderRepository orderRepository,
                                PaymentRepository paymentRepository){
        FinishOrderData finishOrderData = finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);

        Pay pay = paymentRepository.save(PayFixture.
                builder().
                order(finishOrderData.getOrder()).
                payAmount(finishOrderData.getOrder().getTotalAmount()).
                build().
                create());

        return pay;
    }

    public static Pay reflectPayCommonResultAfterCallingApi(MemberRepository memberRepository,
                                                            ProductRepository productRepository,
                                                            OrderItemRepository orderItemRepository,
                                                            OrderRepository orderRepository,
                                                            PaymentRepository paymentRepository){
        Pay pay = finishPayPreProcess(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository);

        PayResult.PayResultCommon payResultCommon = PayResult.PayResultCommon.builder().
                payMethod(PayMethod.CARD).
                approvedAt(OffsetDateTime.now()).
                build();

        pay.reflectPaySuccess(payResultCommon);

        Pay updatedPay = paymentRepository.save(pay);

        return updatedPay;
    }

}
