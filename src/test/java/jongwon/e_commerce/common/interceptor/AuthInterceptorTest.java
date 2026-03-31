package jongwon.e_commerce.common.interceptor;

import jongwon.e_commerce.support.TestController;
import jongwon.e_commerce.member.application.MemberService;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.mock.fake.FakeMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthInterceptorTest {

    MemberService memberService;
    TestController testController;
    MockMvc mockMvc;

    @BeforeEach
    void init(){
        memberService = MemberService.builder().memberRepository(new FakeMemberRepository()).build();
        testController = new TestController();
        mockMvc = MockMvcBuilders.standaloneSetup(testController).
                addInterceptors(new AuthInterceptor(memberService)).
                build();
        memberService.create(
                MemberCreate.builder().
                memberName("LeeJongWon").loginId("wwwl7749").email("dlwhddnjs951@naver.com").
                        addr("경기도 고양시 덕양구").password("1234").build());
    }

    @Test
    void success() throws Exception {
        mockMvc.perform(get("/test/interceptor").
                header("X-MOCK-USER-LOGINID", "wwwl7749")).
                andExpect(status().isOk()).
                andExpect(content().string("ok"));
    }

    @Test
    void no_header() throws Exception {
        mockMvc.perform(get("/test/interceptor")).
                andExpect(status().is4xxClientError()).
                andExpect(content().string("authentication header is not included"));
    }

    @Test
    void authenticated_user_detected_in_interceptor() throws Exception{
        mockMvc.perform(get("/test/interceptor").
                        header("X-MOCK-USER-LOGINID", "dlwhddnjs951")).
                andExpect(status().is4xxClientError()).
                andExpect(content().string("unauthenticated user"));
    }

}