package jongwon.e_commerce.support.fixture;

import jongwon.e_commerce.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberFixture {

    @Builder.Default
    private String loginId = "testUser";

    @Builder.Default
    private String password = "password123";

    @Builder.Default
    private String memberName = "홍길동";

    @Builder.Default
    private String email = "test@example.com";

    @Builder.Default
    private String addr = "서울시 강남구";

    public Member create() {
        return Member.builder()
                .loginId(loginId)
                .password(password)
                .memberName(memberName)
                .email(email)
                .addr(addr)
                .build();
    }
}
