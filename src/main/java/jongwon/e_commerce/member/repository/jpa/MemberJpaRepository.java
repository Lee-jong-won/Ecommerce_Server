package jongwon.e_commerce.member.repository.jpa;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.jpa.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
}
