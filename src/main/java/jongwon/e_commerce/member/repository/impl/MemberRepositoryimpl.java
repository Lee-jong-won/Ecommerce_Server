package jongwon.e_commerce.member.repository.impl;

import jongwon.e_commerce.common.exception.ResourceNotFoundException;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.exception.MemberNotFoundException;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.member.repository.jpa.MemberJpaRepository;
import jongwon.e_commerce.member.repository.jpa.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class MemberRepositoryimpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member getById(long id){
        return findById(id).orElseThrow(()
        -> new ResourceNotFoundException("Users", id));
    }

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(MemberEntity.from(member)).toModel();
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id).map(MemberEntity::toModel);
    }
}
