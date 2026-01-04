package jongwon.e_commerce.member.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_login_id", columnNames = "login_id"),
                @UniqueConstraint(name = "uq_email", columnNames = "email")
        }
)
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

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
