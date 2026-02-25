package jongwon.e_commerce.member.repository;

import jongwon.e_commerce.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);
    Optional<Member> findById(Long id);
    void clearStore();
}
