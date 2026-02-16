package jongwon.e_commerce.member.repository.jpa;

import jongwon.e_commerce.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
