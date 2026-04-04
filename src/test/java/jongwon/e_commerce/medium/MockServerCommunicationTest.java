package jongwon.e_commerce.medium;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MockServerCommunicationTest {

    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void warmup_header_포함시_mockPG로부터_500ms뒤_응답이_돌아옴() throws Exception {
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        mockMvc.perform(post("/test/payment/confirm").
                header("x-test-phase", "warmup").
                        contentType(MediaType.APPLICATION_JSON).
                content(objectMapper.writeValueAsString(request))).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.approvedAt").value("2026-04-02T12:00:00+09:00")).
                andExpect(jsonPath("$.method").value("휴대폰")).
                andExpect(jsonPath("$.mobilePhone.customerMobilePhone").value("01012345678")).
                andExpect(jsonPath("$.mobilePhone.settlementStatus").value("SETTLED")).
                andExpect(jsonPath("$.mobilePhone.receiptUrl").value("http://receipt.url"));
    }

    @Test
    void rampup_header_포함시_MockPG로부터_1500ms뒤_응답이_돌아옴() throws Exception {
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        mockMvc.perform(post("/test/payment/confirm").
                        header("x-test-phase", "rampup").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.approvedAt").value("2026-04-02T12:00:00+09:00")).
                andExpect(jsonPath("$.method").value("휴대폰")).
                andExpect(jsonPath("$.mobilePhone.customerMobilePhone").value("01012345678")).
                andExpect(jsonPath("$.mobilePhone.settlementStatus").value("SETTLED")).
                andExpect(jsonPath("$.mobilePhone.receiptUrl").value("http://receipt.url"));
    }

    @Test
    void sustained_header_포함시_MockPG로부터_3초뒤_응답이_돌아옴() throws Exception {
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        mockMvc.perform(post("/test/payment/confirm").
                        header("x-test-phase", "sustained").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.approvedAt").value("2026-04-02T12:00:00+09:00")).
                andExpect(jsonPath("$.method").value("휴대폰")).
                andExpect(jsonPath("$.mobilePhone.customerMobilePhone").value("01012345678")).
                andExpect(jsonPath("$.mobilePhone.settlementStatus").value("SETTLED")).
                andExpect(jsonPath("$.mobilePhone.receiptUrl").value("http://receipt.url"));
    }

    @Test
    void end_of_load_header_포함시_MockPG로부터_1초뒤_응답이_돌아옴() throws Exception {
        // given
        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                10000);

        // when
        mockMvc.perform(post("/test/payment/confirm").
                        header("x-test-phase", "end-of-load").
                        contentType(MediaType.APPLICATION_JSON).
                        content(objectMapper.writeValueAsString(request))).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.approvedAt").value("2026-04-02T12:00:00+09:00")).
                andExpect(jsonPath("$.method").value("휴대폰")).
                andExpect(jsonPath("$.mobilePhone.customerMobilePhone").value("01012345678")).
                andExpect(jsonPath("$.mobilePhone.settlementStatus").value("SETTLED")).
                andExpect(jsonPath("$.mobilePhone.receiptUrl").value("http://receipt.url"));
    }
}
