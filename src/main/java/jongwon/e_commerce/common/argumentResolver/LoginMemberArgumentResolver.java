package jongwon.e_commerce.common.argumentResolver;

import jakarta.servlet.http.HttpServletRequest;
import jongwon.e_commerce.member.application.MemberService;
import jongwon.e_commerce.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;

    public LoginMemberArgumentResolver(MemberService memberService){
        this.memberService = memberService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");
        boolean hasLoginAnnotation =
                parameter.hasParameterAnnotation(LoginMember.class);
        boolean hasMemberType =
                Member.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter,
                                            @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new BadRequestException("잘못된 요청입니다.");
        }

        String loginId = request.getHeader("X-MOCK-USER-LOGINID");
        if (loginId == null || loginId.equals("")) {
            throw new BadRequestException("Login id is needed");
        }

        Member member = memberService.getByLoginId(loginId);
        return member;
    }
}
