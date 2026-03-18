package jongwon.e_commerce.member.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCreate {

    private String loginId;
    private String password;
    private String memberName;
    private String email;
    private String addr;

    @Builder
    public MemberCreate(
            @JsonProperty("loginId") String loginId,
            @JsonProperty("password") String password,
            @JsonProperty("memberName") String memberName,
            @JsonProperty("email") String email,
            @JsonProperty("addr") String addr
    ){
        this.loginId = loginId;
        this.password = password;
        this.memberName = memberName;
        this.email = email;
        this.addr = addr;
    }

}
