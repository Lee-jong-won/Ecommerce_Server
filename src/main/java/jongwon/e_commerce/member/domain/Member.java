package jongwon.e_commerce.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "member")
public class Member {
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

    public static Member create(String loginId, String password,
                               String memberName, String email, String addr){
        Member member = new Member();
        member.loginId = loginId;
        member.password = password;
        member.memberName = memberName;
        member.email = email;
        member.addr = addr;
        return member;
    }
}
