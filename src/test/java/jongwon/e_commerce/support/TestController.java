package jongwon.e_commerce.support;

import jongwon.e_commerce.common.argumentResolver.LoginMember;
import jongwon.e_commerce.member.domain.Member;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/interceptor")
    public String testInterceptor(){
        return "ok";
    }

    @GetMapping("/test/argumentResolver")
    public String testArgumentResolver(@LoginMember Member member) { return member.getLoginId();}

    @GetMapping("/test/argumentResolver/nonParameter")
    public String testArgumentResolverNonParameter(Member member) { return member.getLoginId();}
}
