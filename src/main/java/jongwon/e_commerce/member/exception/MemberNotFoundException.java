package jongwon.e_commerce.member.exception;

import jongwon.e_commerce.order.domain.common.exception.DomainException;

public class MemberNotFoundException extends DomainException {
    public MemberNotFoundException(Long memberId) {
        super("존재하지 않는 회원입니다. memberId=" + memberId);
    }
}
