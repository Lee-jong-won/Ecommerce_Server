package jongwon.e_commerce.common.config;

import jongwon.e_commerce.common.argumentResolver.LoginMemberArgumentResolver;
import jongwon.e_commerce.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final MemberService memberService;
    // private final EntityManagerFactory emf;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*OpenEntityManagerInViewInterceptor openEntityManagerInViewInterceptor = new OpenEntityManagerInViewInterceptor();
        openEntityManagerInViewInterceptor.setEntityManagerFactory(emf);
        registry.addWebRequestInterceptor(openEntityManagerInViewInterceptor);*/
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(createLoginMemberArgumentResolver(memberService));
    }

    @Bean
    public LoginMemberArgumentResolver createLoginMemberArgumentResolver(MemberService memberService){
        return new LoginMemberArgumentResolver(memberService);
    }

}
