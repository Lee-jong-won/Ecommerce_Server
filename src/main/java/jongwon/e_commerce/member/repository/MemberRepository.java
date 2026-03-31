package jongwon.e_commerce.member.repository;

import jongwon.e_commerce.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {
    Member getById(long id);
    Member save(Member member);
    Optional<Member> findByloginId(String loginId);
    Optional<Member> findById(Long id);
}
