package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.payment.exception.UnsupportedPGException;

public enum PGType {
    TOSS,
    NICE;

    public static PGType from(String pgType){
        if("TOSS".equals(pgType))
            return TOSS;

        if("NICE".equals(pgType))
            return NICE;

        throw new UnsupportedPGException("지원하지 않는 PG사입니다");
    }
}
