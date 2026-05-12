package jongwon.e_commerce.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
public class Member {

    private Long memberId;
    private String loginId;
    private String password;
    private String memberName;
    private String email;
    private String addr;
    private LocalDateTime lastLoginAt;

    public static Member createMember(MemberCreate memberCreate){
       return Member.builder().
               loginId(memberCreate.getLoginId()).
               password(memberCreate.getPassword()).
               memberName(memberCreate.getMemberName()).
               email(memberCreate.getEmail()).
               addr(memberCreate.getAddr()).build();
    }

    public Member login(LocalDateTime lastLoginAt){
        return Member.builder().
                memberId(memberId).
                password(password).
                loginId(loginId).
                memberName(memberName).
                email(email).
                addr(addr).
                lastLoginAt(lastLoginAt).build();
    }

    @Override
    public int hashCode() {
        return memberId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if( !(obj instanceof Member) ) return false;
        Member member = (Member) obj;
        return member.getMemberId() != null && Objects.equals(member.getMemberId(), memberId);
    }
}
