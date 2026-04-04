package jongwon.e_commerce.config;

import jongwon.e_commerce.common.argumentResolver.LoginMember;
import jongwon.e_commerce.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@TestConfiguration
public class TestControllerConfig {

    @RestController
    @RequiredArgsConstructor
    public static class TestController {

        @GetMapping("/test/argumentResolver")
        public String testArgumentResolver(@LoginMember Member member) { return member.getLoginId();}

        @GetMapping("/test/argumentResolver/nonParameter")
        public String testArgumentResolverNonParameter(Member member) { return member.getLoginId();}
    }

}
