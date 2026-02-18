package jongwon.e_commerce.member.repository;

import jongwon.e_commerce.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {

    Member save(String loginId, String password,
                String memberName, String email, String addr);
    Optional<Member> findById(Long id);
}
