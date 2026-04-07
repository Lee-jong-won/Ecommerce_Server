package jongwon.e_commerce.common.commandLineRunner;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.application.OrderExecutor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderItemCreate;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class TestDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderExecutor orderExecutor;

    @Override
    public void run(String... args) throws Exception {
        for(int i = 1; i <= 500; i++){
            memberRepository.save(Member.createMember(MemberCreate.builder().
                    memberName("user" + i ).
                    loginId("user-" + i).
                    password("pass").
                    email("user" + i + "@test.com").
                    addr("Seoul").build()));
        }

        for(int i = 1; i <= 500; i++){
            Product product = Product.from("product-" + i, 1000);
            product.changeStock(2000);
            product.startSelling();
            productRepository.save(product);
        }

        for(long i = 1; i <= 500L; i++){
            Member member = memberRepository.getById(i);
            List<OrderItemCreate> orderItemCreates = List.of(new OrderItemCreate(i, 1));
            orderExecutor.order(member, "orderName-" + i, "orderId-" + i, orderItemCreates);
        }
    }
}
