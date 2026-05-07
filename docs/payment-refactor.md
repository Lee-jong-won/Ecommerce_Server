# Payment 모듈 리팩토링

## 변경 개요

결제 승인 흐름을 Outcome 객체 반환 패턴에서 Exception 기반 패턴으로 전환하고,  
결제 요청 라이프사이클 추적을 위한 `PayRequest` 도메인을 도입하였습니다.  
또한 중복 결제 방지를 위한 멱등성 처리 레이어가 추가되었습니다.

---

## 1. 도메인 모델 분리: `Pay` → `Pay` + `PayRequest`

### 기존
`Pay` 도메인이 결제 요청 상태 관리(PENDING → COMPLETE/FAILED/TIMEOUT)와  
결제 완료 데이터(PG 응답 정보)를 모두 담당.

### 변경
| 클래스 | 역할 |
|--------|------|
| `PayRequest` | 결제 요청의 라이프사이클 관리 (상태 전이: PENDING → COMPLETE/FAILED/UNKNOWN/REFUND) |
| `Pay` | PG 승인 완료 후 생성되는 불변 결제 기록 |

`PayRequest`가 주문(`Order`)과 결제 요청 정보를 소유하고,  
`Pay`는 `PayRequest`를 참조하는 구조로 변경되었습니다.

### `PayStatus` 변경
- `TIME_OUT` 제거 → `UNKNOWN` 으로 대체  
  (타임아웃 발생 시 PG사의 실제 처리 여부가 불명확하므로 `UNKNOWN`이 의미상 더 정확)

---

## 2. Outcome 패턴 → Exception 패턴 전환

### 기존 구조
```
executePayApprove() → PayApproveOutcome 반환
→ outcomeHandlers 체인에서 outcome 타입에 따라 Pay 상태 업데이트
```

삭제된 클래스:
- `domain/approve/outcome/` 패키지 전체 (PayApproveOutcome, PayApproveFail, PayApproveSuccess 등)
- `application/approve/handler/` 패키지 전체 (PayOutcomeHandler, PaySuccessHandler, PayFailHandler, PayTimeoutHandler)
- `controller/response/PayOutcomeHttpMapper`

### 변경 구조
```
executePayApprove() → PayResult 반환 (성공) 또는 PayApproveException throw (실패)
→ catch 블록에서 예외 타입에 따라 PayRequest 상태 업데이트
```

### 새로운 예외 계층

```
PayApproveException (abstract)
├── PayClientException       : 클라이언트 오류 (잔액 부족, 카드 오류) — PayErrorCode 포함
├── PayGatewayException      : PG사 내부 오류
├── PayServerException       : 서버/네트워크 리소스 오류 (커넥션 타임아웃, 풀 고갈 등)
└── PayUnknownOutcomeException (abstract)
    └── PayNetworkException  : 읽기 타임아웃 (PG 처리 여부 불명확)
```

`PayErrorCode`는 `domain/approve/outcome/fail/` → `exception/` 패키지로 이동하고,  
`UNKNOWN_ERROR_CODE`, `JSON_PARSING_ERROR`, `INVALID_ERROR_RESPONSE` 코드가 제거되었습니다.

---

## 3. 서비스 계층 재구성

### 기존
```
PaymentService        — 결제 전 검증 및 Pay 생성
PaymentApprovalService — 실행 + outcome handler 호출
```

### 변경
```
PayPreprocessor        — 결제 전 검증 및 PayRequest 생성 (PaymentService 대체)
PaymentApprovalService — 흐름 조율 (PG 실행 + 예외별 상태 처리)
PaySuccessProcessor    — 결제 성공 후처리 (Pay 생성 + 재고 차감) @Transactional
PaymentCreator         — Pay 엔티티 생성 및 저장
PayRequestStateManager — PayRequest 상태 전이 전담 (markFailed / markUnknown / markRefund)
```

`PaymentApprovalService`의 예외 처리 흐름:
```java
try {
    PayResult result = executor.executePayApprove(attempt);
    paySuccessProcessor.process(payRequest, result);  // 성공
} catch (PayUnknownOutcomeException e) {
    payRequestStateManager.markUnknown(payRequest);   // 결과 불명확
    throw e;
} catch (PayClientException | PayGatewayException | PayServerException e) {
    payRequestStateManager.markFailed(payRequest);    // 실패 확정
    throw e;
}
```

---

## 4. 멱등성 처리 추가 (`controller/idempotency/`)

동일한 결제 요청이 중복으로 들어올 경우를 처리하기 위한 레이어.

| 클래스 | 설명 |
|--------|------|
| `PaymentIdempotencyGuard` | 락 획득 → 캐시 확인 → 결제 실행 → 응답 캐싱 순서로 처리 |
| `LockManager` / `LocalLockManager` | `ConcurrentHashMap<PayApproveAttempt, ReentrantLock>` 기반 로컬 락 |
| `PaymentResponseBodyRepository` | Caffeine 캐시 기반 응답 저장소 |

- 락 획득 대기 5초 초과 시 `PaymentTimeoutException` 발생
- 이미 처리된 요청은 캐시에서 즉시 반환 (중복 PG 호출 방지)
- `PaymentCacheConfig`가 Caffeine 캐시 Bean을 설정

---

## 5. 컨트롤러 예외 핸들링 강화

`PayOutcomeHttpMapper` 삭제 후, `@ExceptionHandler`로 예외 타입별 HTTP 응답을 직접 처리:

| 예외 | HTTP 상태 | 사유 |
|------|-----------|------|
| `PayClientException` | 400 Bad Request | 클라이언트 입력 오류 |
| `PayGatewayException` | 502 Bad Gateway | PG사 오류 |
| `PayUnknownOutcomeException` | 504 Gateway Timeout | 네트워크 타임아웃, 결과 불명확 |
| `PayServerException` | 503 Service Unavailable | 서버 리소스 부족 |
| `PaymentTimeoutException` | 409 Conflict | 중복 요청 대기 타임아웃 |
| `DataAccessException` | 500 Internal Server Error | DB 오류 |

---

## 6. 인프라 변경

- **`PayRequestEntity`** + **`PayRequestJpaRepository`** + **`PayRequestRepositoryImpl`** 추가  
  → `PayRequest` 도메인의 영속성 처리
- **`PaymentExecutorConfig`** 이동: `infrastructure/gateway/config/` → `payment/config/`
- **`CommonPaymentExecutor`**: `PayApproveOutcome` 반환 → `PayResult` 반환 / `RestClientException`을 `PayApproveException`으로 변환하여 throw
