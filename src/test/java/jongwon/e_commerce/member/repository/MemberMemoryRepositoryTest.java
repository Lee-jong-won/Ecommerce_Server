package jongwon.e_commerce.member.repository;

import jongwon.e_commerce.member.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberMemoryRepositoryTest {

    MemberMemoryRepository memberMemoryRepository = new MemberMemoryRepository();

    @AfterEach
    void afterEach(){
        memberMemoryRepository.clearStore();
    }

    @Test
    void 회원_저장_테스트(){
        //given
        String loginId = "wwwl7749";
        String passWord = "1234";
        String memberName = "이종원";
        String email = "dlwhddnjs951@naver.com";
        String addr = "경기도 고양시 덕양구";

        //when
        Member member = memberMemoryRepository.save(loginId, passWord, memberName, email, addr);

        //then
        Member find = memberMemoryRepository.findById(member.getMemberId()).get();
        assertThat(find).isEqualTo(member);
    }
}