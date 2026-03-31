package jongwon.e_commerce.member.application;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.member.domain.LoginForm;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.domain.MemberCreate;
import jongwon.e_commerce.member.repository.MemberRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Builder
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getByLoginId(String loginId){
        return memberRepository.findByloginId(loginId).orElseThrow(
                () -> new ResourceNotFoundException("member", loginId)
        );
    }

    public boolean existsByLoginId(String loginId){
        return memberRepository.findByloginId(loginId).isPresent();
    }

    @Transactional
    public Member create(MemberCreate memberCreate){
        Member member = Member.createMember(memberCreate);
        member = memberRepository.save(member);
        return member;
    }

    @Transactional
    public void login(LoginForm loginForm){
        Member member = memberRepository.findByloginId(loginForm.getLoginId()).
                orElseThrow(
                        () -> new ResourceNotFoundException("User", loginForm.getLoginId())
                );
        member.login(LocalDateTime.now());
    }
}
