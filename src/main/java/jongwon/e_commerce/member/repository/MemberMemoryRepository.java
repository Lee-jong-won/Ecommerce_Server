package jongwon.e_commerce.member.repository;

import jongwon.e_commerce.member.domain.Member;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemberMemoryRepository implements MemberRepository{
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    @Override
    public Member save(String loginId, String password, String memberName, String email, String addr) {
        Member member = Member.create(loginId, password, memberName, email, addr);
        member.setMemberId(++sequence);
        store.put(member.getMemberId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void clearStore(){
        store.clear();
    }
}
