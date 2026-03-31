package jongwon.e_commerce.member.controller;

import jongwon.e_commerce.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberResponse {

    private String memberName;
    private String addr;


    public static MemberResponse from(Member member){
        return MemberResponse.builder().
                memberName(member.getMemberName())
                .addr(member.getAddr()).build();
    }

}
