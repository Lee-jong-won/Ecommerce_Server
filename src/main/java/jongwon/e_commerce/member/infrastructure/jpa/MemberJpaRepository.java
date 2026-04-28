package jongwon.e_commerce.member.infrastructure.jpa;

import jongwon.e_commerce.member.infrastructure.jpa.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByLoginId(String loginId);

}
