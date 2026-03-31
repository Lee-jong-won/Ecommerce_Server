package jongwon.e_commerce.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jongwon.e_commerce.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String loginId = request.getHeader("X-MOCK-USER-LOGINID");
        log.info("HEADER: " + request.getHeader("X-MOCK-USER-LOGINID"));
        if(loginId == null || loginId.equals("")){
            response.setStatus(401);
            response.getWriter().print("authentication header is not included");
            return false;
        }

        boolean exists = memberService.existsByLoginId(loginId);
        if (!exists) {
            response.setStatus(401);
            response.getWriter().print("unauthenticated user");
            return false;
        }

        return true;
    }
}
