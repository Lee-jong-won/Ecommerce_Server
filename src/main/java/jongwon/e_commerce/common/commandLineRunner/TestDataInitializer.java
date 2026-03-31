package jongwon.e_commerce.common.commandLineRunner;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        for(int i = 1; i <= 1000; i++){
            memberRepository.save(Member.createMember(MemberCreate.builder().
                    memberName("user" + i ).
                    loginId("user-" + i).
                    password("pass").
                    email("user" + i + "@test.com").
                    addr("Seoul").build()));
        }

        for(int i = 1; i <= 500; i++){
            productRepository.save(
                    Product.from("product-" + i, 1000)
            );
        }
    }
}
