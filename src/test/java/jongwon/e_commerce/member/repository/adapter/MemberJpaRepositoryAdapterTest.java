package jongwon.e_commerce.member.repository.adapter;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.exception.MemberNotFoundException;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.member.repository.jpa.MemberJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MemberJpaRepositoryAdapterTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void 멤버가_정상적으로_저장된다(){
        // given
        MemberRepository memberRepository = new MemberJpaRepositoryAdapter(memberJpaRepository);

        // when
        Member member = memberRepository.save("1234", "1234", "이종원", "dlwhddnjs951@naver.com",
                "경기도 고양시 덕양구");

        // then
        Member findMember = memberJpaRepository.findById(member.getMemberId()).orElseThrow();

        assertEquals(member.getMemberId(), findMember.getMemberId());
        assertEquals(member.getPassword(), findMember.getPassword());
        assertEquals(member.getMemberName(), findMember.getMemberName());
        assertEquals(member.getEmail(), findMember.getEmail());
        assertEquals(member.getAddr(), findMember.getAddr());
    }

    @Test
    void 멤버가_정상적으로_조회된다(){
        //given
        MemberRepository memberRepository = new MemberJpaRepositoryAdapter(memberJpaRepository);
        Member member = Member.create("1234", "1234", "이종원", "dlwhddnjs951@naver.com",
                "경기도 고양시 덕양구");
        memberJpaRepository.save(member);

        //when && then
        assertDoesNotThrow(() -> memberRepository.findById(member.getMemberId()));
    }

    @Test
    void 존재하지_않는_멤버_조회시_예외_발생(){
        //given
        MemberRepository memberRepository = new MemberJpaRepositoryAdapter(memberJpaRepository);

        //when && then
        assertThrows(MemberNotFoundException.class, () -> memberRepository.findById(1L));
    }

}