package jongwon.e_commerce.support.fixture;

import jongwon.e_commerce.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class MemberFixture {

    @Builder.Default
    private Long memberId = null;

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

    @Builder.Default
    private LocalDateTime lastLoginAt = LocalDateTime.of(2016, 9, 17, 4, 30);

    public Member create() {
        return Member.builder()
                .memberId(memberId)
                .loginId(loginId)
                .password(password)
                .memberName(memberName)
                .email(email)
                .addr(addr)
                .lastLoginAt(lastLoginAt)
                .build();
    }

}
