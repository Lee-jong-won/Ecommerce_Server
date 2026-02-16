package jongwon.e_commerce.member.repository;

import jongwon.e_commerce.member.domain.Member;

public interface MemberRepository {

    Member save(String loginId, String password,
                String memberName, String email, String addr);
    Member findById(Long id);
}
