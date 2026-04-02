package jongwon.e_commerce.common.argumentResolver;

import jongwon.e_commerce.common.controller.ExceptionControllerAdvice;
import jongwon.e_commerce.config.TestControllerConfig;
import jongwon.e_commerce.member.application.MemberService;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.mock.fake.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginMemberArgumentResolverTest {

    MemberService memberService;
    TestControllerConfig.TestController testController;
    MockMvc mockMvc;

    @BeforeEach
    void init(){
        memberService = MemberService.builder().memberRepository(new FakeMemberRepository()).build();
        testController = new TestControllerConfig.TestController();
        mockMvc = MockMvcBuilders.standaloneSetup(testController).
                setControllerAdvice(new ExceptionControllerAdvice()).
                setCustomArgumentResolvers(new LoginMemberArgumentResolver(memberService)).
                build();
        memberService.create(
                MemberCreate.builder().
                        memberName("LeeJongWon").loginId("wwwl7749").email("dlwhddnjs951@naver.com").
                        addr("경기도 고양시 덕양구").password("1234").build());
    }

    @Test
    void 인증된_사용자는_통과() throws Exception{
        mockMvc.perform(get("/test/argumentResolver").
                        header("X-MOCK-USER-LOGINID", "wwwl7749")).
                andExpect(status().isOk()).
                andExpect(content().string("wwwl7749"));
    }

    @Test
    void 인증되지_않은_사용자는_API사용_불가() throws Exception{
        mockMvc.perform(get("/test/argumentResolver").
                        header("X-MOCK-USER-LOGINID", "dlwhddnjs951")).
                andExpect(status().is4xxClientError()).
                andExpect(content().string("jongwon.e_commerce.common.exception.ResourceNotFoundException: Could not find dlwhddnjs951 from member"));
    }

    @Test
    void email_헤더가_없거나_빈_문자열이면_예외_응답이_돌아옴() throws Exception {
        mockMvc.perform(get("/test/argumentResolver")).
                andExpect(status().is4xxClientError()).
                andExpect(content().string("org.apache.coyote.BadRequestException: Login id is needed"));
    }

    @Test
    void 어노테이션이_없는_메소드에서는_동작하지_않음() throws Exception{
        mockMvc.perform(get("/test/argumentResolver/nonParameter").
                        header("X-MOCK-USER-LOGINID", "wwwl7749")).
                andExpect(status().isOk()).
                andExpect(content().string(""));
    }
}