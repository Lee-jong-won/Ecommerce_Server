package jongwon.e_commerce.member.domain;

import jongwon.e_commerce.support.fixture.MemberFixture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @Test
    void MemberCreate로부터_Member가_정상적으로_생성된다() {
        // given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("testUser")
                .password("password123")
                .memberName("홍길동")
                .email("test@example.com")
                .addr("서울시 강남구")
                .build();

        // when
        Member member = Member.createMember(memberCreate);

        // then
        assertThat(member).isNotNull();
        assertThat(member.getLoginId()).isEqualTo("testUser");
        assertThat(member.getPassword()).isEqualTo("password123");
        assertThat(member.getMemberName()).isEqualTo("홍길동");
        assertThat(member.getEmail()).isEqualTo("test@example.com");
        assertThat(member.getAddr()).isEqualTo("서울시 강남구");
    }

    @Test
    void login시_lastLogin값이_변경된다(){
        // given
        Member member = MemberFixture.builder().build().create();
        LocalDateTime loginAt = LocalDateTime.of(2026, 4, 3, 12, 30);

        // when
        Member loginMember = member.login(loginAt);

        // then
        assertThat(loginMember.getLastLoginAt()).isEqualTo(loginAt);
    }

}