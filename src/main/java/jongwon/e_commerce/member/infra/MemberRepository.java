package jongwon.e_commerce.member.infra;

import jongwon.e_commerce.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
