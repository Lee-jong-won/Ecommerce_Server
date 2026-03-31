package jongwon.e_commerce.member.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginForm {
    @NotEmpty
    private String loginId;
    @NotEmpty
    private String password;
}
