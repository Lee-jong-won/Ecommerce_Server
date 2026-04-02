package jongwon.e_commerce.common.interceptor;

import jongwon.e_commerce.common.controller.TestCommonController;
import jongwon.e_commerce.config.TestControllerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TestPhaseInterceptorTest {
    TestCommonController testController;
    MockMvc mockMvc;

    @BeforeEach
    void init(){
        testController = new TestCommonController();
        mockMvc = MockMvcBuilders.
                standaloneSetup(testController).
                addInterceptors(new TestPhaseInterceptor()).
                build();
    }

    @Test
    void 쓰레드_로컬에_값이_정상적으로_저장되고_이후에_요청이_끝나면_쓰레드_로컬에서_값이_삭제된다() throws Exception {
        mockMvc.perform(get("/test/interceptor").
                header("x-test-phase", "warmup")).
                andExpect(status().isOk()).andExpect(content().string("warmup"));
        assertThat(TestPhaseContext.get()).isNull();
    }

}