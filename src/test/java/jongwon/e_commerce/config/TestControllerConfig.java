package jongwon.e_commerce.config;

import jongwon.e_commerce.common.argumentResolver.LoginMember;
import jongwon.e_commerce.common.interceptor.TestPhaseContext;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@TestConfiguration
public class TestControllerConfig {

    @RestController
    @RequiredArgsConstructor
    public static class TestController {
        @GetMapping("/test/interceptor")
        public String testInterceptor(){
            return TestPhaseContext.get();
        }

        @GetMapping("/test/argumentResolver")
        public String testArgumentResolver(@LoginMember Member member) { return member.getLoginId();}

        @GetMapping("/test/argumentResolver/nonParameter")
        public String testArgumentResolverNonParameter(Member member) { return member.getLoginId();}
    }


}
