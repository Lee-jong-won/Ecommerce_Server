package jongwon.e_commerce.member.repository.jpa;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.jpa.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByLoginId(String loginId);

}
