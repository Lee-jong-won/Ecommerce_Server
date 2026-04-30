## Tech Stack (기술 스택)

- Backend : Spring, JPA
- DB : MySQL

## Architecture (패키지 구조)

모노레포 구조

...
src/
    main/
        /java
            /jongwon
                /e_commerce
                    /common
                        /argumentResolver
                        /commandLineRunner
                        /config
                        /controller 
                        /exception  
                        /filter
                    /external 
                        /http
                            /client
                            /policy
                    /member
                        /application
                        /controller
                        /domain
                        /exception
                        /infrastructure
                        /repository
                    /order
                        /application
                        /controller
                        /domain
                        /exception
                        /infrastructure
                        /repository
                    /payment
                        /application
                        /controller
                        /domain
                        /exception
                        /gateway
                        /infrastructure
                        /repository
                    /product
                        /application
                        /domain
                        /exception
                        /infrastructure
                        /repository
...

## Domain Context (도메인 컨텍스트)
- **member** : 유저.
- **product**: 상품. 
- **order**: 주문.
- **payment**: 결제. 
- **Cart -> Order -> Payment**: 장바구니 -> 주문 생성 -> 결제 순서

## Custom Commands (커스텀 커맨드)

| 커맨드 | 설명 |
|--------|------|
| `/ship` | 전체 테스트 실행 → 모두 통과 시 commit & push (현재 브랜치) |


