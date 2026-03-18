package jongwon.e_commerce.member.repository.jpa.entity;

import jakarta.persistence.*;
import jongwon.e_commerce.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "member_name", nullable = false, length = 50)
    private String memberName;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 255)
    private String addr;

    public static MemberEntity from(Member member){
        MemberEntity memberEntity = new MemberEntity();

        memberEntity.memberId = member.getMemberId();
        memberEntity.addr = member.getAddr();
        memberEntity.email = member.getEmail();
        memberEntity.memberName = member.getMemberName();
        memberEntity.password = member.getMemberName();
        memberEntity.loginId = member.getLoginId();

        return memberEntity;
    }

    public Member toModel(){
        return Member.builder()
                .memberId(memberId)
                .loginId(loginId)
                .email(email)
                .password(password)
                .memberName(memberName)
                .addr(addr).build();
    }
}
