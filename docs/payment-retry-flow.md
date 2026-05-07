# 결제 실패 및 재시도 흐름

> 정상 결제 흐름은 `payment-success-flow.md` 참고

---

## 기본 원칙

- **주문 API는 재시도 시 호출하지 않는다.** 주문 API를 다시 호출하면 새 주문이 생성된다.
- 결제 재시도는 결제 API만 호출한다.
- 프론트는 결제 실패 응답의 `retryStrategy`를 기준으로 다음 동작을 결정한다.

---

## 결제 API 예외 흐름

PG 호출 결과에 따라 다음 네 가지 예외가 발생할 수 있다.

| 예외 | 원인 | HTTP | retryStrategy |
|------|------|------|---------------|
| `PayClientException` | 잔액 부족, 카드 오류 등 클라이언트 귀책 | 400 | `NEW_PAYMENT_KEY` |
| `PayGatewayException` | PG사 내부 오류 | 502 | `SAME_PAYMENT_KEY` |
| `PayServerException` | 커넥션 타임아웃, 풀 고갈 등 인프라 오류 | 500 | `SAME_PAYMENT_KEY` |
| `PayUnknownOutcomeException` | ReadTimeout — PG 처리 여부 불명확 | 504 | `SAME_PAYMENT_KEY` |

예외 발생 시 `PayRequestStateManager`가 PayRequest와 Order 상태를 갱신한다.

```
PayClientException        → PayRequest: BUSINESS_FAILED / Order: FAIL
PayGatewayException       → PayRequest: SERVER_FAILED   / Order: FAIL
PayServerException        → PayRequest: SERVER_FAILED   / Order: FAIL
PayUnknownOutcomeException → PayRequest: UNKNOWN        / Order: PAYMENT_PENDING (유지)
```

**실패 응답 형식**

```json
// 4xx / 5xx
{
  "code": "INVALID_CARD",
  "message": "카드 정보가 잘못 되었습니다",
  "retryStrategy": "NEW_PAYMENT_KEY" | "SAME_PAYMENT_KEY"
}
```

| retryStrategy | 의미 | 이유 |
|---------------|------|------|
| `NEW_PAYMENT_KEY` | PG 위젯 재호출 후 새 paymentKey로 재시도 | 결제 자체가 거부됐으므로 새 결제 수단 필요 |
| `SAME_PAYMENT_KEY` | 동일 paymentKey로 재시도 | 인프라/네트워크 문제이므로 같은 정보로 재시도 가능 |

---

## 시나리오별 흐름

### 1. 비즈니스 실패 후 재시도 (잔액 부족, 카드 오류)

> 같은 주문에 대해 다른 결제 수단으로 재시도한다.

```
[프론트]                              [서버]
  │                                    │
  ├─ POST /api/payment ──────────────► │  PayRequest 생성 (PENDING)
  │  { orderId, paymentKey: "KEY-1" }  │  Order → PAYMENT_PENDING
  │                                    │  PG에서 카드 오류 응답
  │                                    │  PayRequest → BUSINESS_FAILED
  │◄───── 400 실패 응답 ───────────────┤  Order → FAIL
  │  { code: "INVALID_CARD",           │
  │    retryStrategy: "NEW_PAYMENT_KEY"}│
  │                                    │
  │  orderId 유지, paymentKey 파기      │  ※ 주문 API 호출 없음
  │                                    │
  ├─ PG 위젯 재호출 (같은 orderId)     │
  │◄─── 새 paymentKey: "KEY-2" ───────┤ (PG사)
  │                                    │
  ├─ POST /api/payment ──────────────► │  동일 orderId, 새 paymentKey
  │  { orderId, paymentKey: "KEY-2" }  │  기존 BUSINESS_FAILED PayRequest 무시
  │                                    │  새 PayRequest 생성 (PENDING)
  │                                    │  Order FAIL → PAYMENT_PENDING
  │◄──────────── 201 성공 응답 ────────┤  PayRequest → COMPLETE, Order → PAID
  │                                    │
  └─ 결제 완료 화면                     │
```

**서버 동작 (PayPreprocessor)**
- `paymentKey: "KEY-2"` 로 조회 → 기존 PayRequest 없음 → 새로 생성
- Order 상태가 `FAIL`이어도 `paymentPending()` 전이 허용

**상태 변화**

| 시점 | OrderStatus | PayRequest(KEY-1) | PayRequest(KEY-2) |
|------|-------------|-------------------|-------------------|
| 최초 결제 진입 | PAYMENT_PENDING | PENDING | — |
| PG 카드 오류 | FAIL | BUSINESS_FAILED | — |
| 재시도 결제 진입 | PAYMENT_PENDING | BUSINESS_FAILED | PENDING |
| PG 승인 완료 | PAID | BUSINESS_FAILED | COMPLETE |

---

### 2. 서버 실패 후 재시도 (커넥션 풀 고갈, 연결 타임아웃)

> 사용자는 "결제 처리 중" 화면만 보고, 프론트가 자동으로 재시도한다.
> 사용자에게 SERVER_ERROR 같은 기술적 메시지를 노출하지 않는다.

```
[프론트]                              [서버]
  │                                    │
  ├─ POST /api/payment ──────────────► │  PayRequest 생성 (PENDING)
  │  { orderId, paymentKey: "KEY-1" }  │  커넥션 풀 고갈 / 연결 타임아웃
  │                                    │  PayRequest → SERVER_FAILED
  │  "결제 처리 중..." 화면 유지        │  Order → FAIL
  │◄───── 500 (retryStrategy:          │
  │        SAME_PAYMENT_KEY) ──────────┤
  │                                    │
  │  [자동 재시도 — 사용자에게 비노출]  │  ※ 주문/PG 위젯 호출 없음
  │                                    │
  ├─ POST /api/payment (자동 재시도) ─► │  기존 SERVER_FAILED PayRequest 발견
  │  { orderId, paymentKey: "KEY-1" }  │  PayRequest → PENDING (초기화)
  │                                    │  Order FAIL → PAYMENT_PENDING
  │                                    │  PG 승인 API 재호출
  │◄──────────── 201 성공 응답 ────────┤  PayRequest → COMPLETE, Order → PAID
  │                                    │
  └─ 결제 완료 화면                     │
```

**재시도 소진 시** (N회 모두 SERVER_FAILED)
```
└─ "결제에 실패했습니다. 잠시 후 다시 시도해 주세요." 안내
   retryStrategy: NEW_PAYMENT_KEY → PG 위젯 재호출 버튼 노출
```

**서버 동작 (PayPreprocessor)**
- `paymentKey: "KEY-1"` 로 조회 → `SERVER_FAILED` PayRequest 발견
- `PayRequest.resetToPending()` → PENDING 초기화
- `Order.resetToPending()` → PAYMENT_PENDING 복귀

**상태 변화**

| 시점 | OrderStatus | PayRequest(KEY-1) |
|------|-------------|-------------------|
| 최초 결제 진입 | PAYMENT_PENDING | PENDING |
| 서버 오류 발생 | FAIL | SERVER_FAILED |
| 자동 재시도 진입 | PAYMENT_PENDING | PENDING (초기화) |
| PG 승인 완료 | PAID | COMPLETE |

---

### 3. ReadTimeout (결과 불명확 / UNKNOWN)

> PG가 멱등성을 보장하므로, 동일 paymentKey 재시도 시 이중 결제가 발생하지 않는다.
> 시나리오 2와 동일하게, 사용자는 "결제 처리 중" 화면만 보고 프론트가 자동으로 재시도한다.

```
[프론트]                              [서버]
  │                                    │
  ├─ POST /api/payment ──────────────► │  PayRequest 생성 (PENDING)
  │  { orderId, paymentKey: "KEY-1" }  │  ReadTimeout 발생
  │                                    │  PayRequest → UNKNOWN
  │  "결제 처리 중..." 화면 유지        │  Order → PAYMENT_PENDING 유지
  │◄───── 504 (retryStrategy:          │
  │        SAME_PAYMENT_KEY) ──────────┤
  │                                    │
  │  [자동 재시도 — 사용자에게 비노출]  │  ※ 주문/PG 위젯 호출 없음
  │                                    │
  ├─ POST /api/payment (자동 재시도) ─► │  기존 UNKNOWN PayRequest 발견
  │  { orderId, paymentKey: "KEY-1" }  │  PayRequest → PENDING (초기화)
  │                                    │  Order PAYMENT_PENDING 유지
  │                                    │  PG 승인 API 재호출 (Idempotency-Key: "KEY-1")
  │                                    │  ├─ PG: 원래 처리됨 → 동일 성공 응답 반환
  │                                    │  └─ PG: 미처리였음 → 새로 처리
  │◄──────────── 201 성공 응답 ────────┤  PayRequest → COMPLETE, Order → PAID
  │                                    │
  └─ 결제 완료 화면                     │
```

**재시도 소진 시** (N회 모두 UNKNOWN)
```
└─ "결제 처리 결과를 확인하지 못했습니다. 잠시 후 마이페이지에서 확인해 주세요." 안내
   ※ 이 시점 서버 PayStatus는 UNKNOWN — 어드민 또는 PG 조회 배치로 후속 처리
```

**서버 동작 (PayPreprocessor)**
- `paymentKey: "KEY-1"` 로 조회 → `UNKNOWN` PayRequest 발견
- `PayRequest.resetToPending()` → PENDING 초기화 (`UNKNOWN → PENDING` 전이 허용)
- Order는 이미 `PAYMENT_PENDING` 상태 → 변경 불필요

**상태 변화**

| 시점 | OrderStatus | PayRequest(KEY-1) |
|------|-------------|-------------------|
| 최초 결제 진입 | PAYMENT_PENDING | PENDING |
| ReadTimeout 발생 | PAYMENT_PENDING (유지) | UNKNOWN |
| 자동 재시도 진입 | PAYMENT_PENDING (유지) | PENDING (초기화) |
| PG 승인 완료 | PAID | COMPLETE |
| *(재시도 소진 시)* | PAYMENT_PENDING (잠금) | UNKNOWN |

**전제 조건**
- PG사의 멱등성 보장이 계약/문서로 확인된 경우에만 적용
- Idempotency-Key는 `orderId`가 아닌 `paymentKey`를 사용해야 한다
  - `orderId` 기반이면 BUSINESS_FAILED 이후 새 paymentKey 재시도 시 PG가 이전 실패 요청과 동일 건으로 인식할 수 있음

---

## 프론트 상태 관리

```javascript
// 결제 플로우에서 유지해야 할 상태
const paymentState = {
  orderId: null,      // 주문 API 응답 후 저장. 재결제 시 재사용
  paymentKey: null,   // PG 위젯 응답 후 저장. SAME_PAYMENT_KEY 재시도 시 재사용
}

const MAX_RETRY = 3

// retryStrategy별 동작
async function handlePaymentResponse(response, retryCount = 0) {
  switch (response.retryStrategy) {
    case "NEW_PAYMENT_KEY":
      // 사용자에게 실패 안내 후 PG 위젯 재호출 (orderId 유지, paymentKey 교체)
      showFailure(response.message)
      openPGWidget(paymentState.orderId)
      break

    case "SAME_PAYMENT_KEY":
      // "결제 처리 중" 화면 유지, 자동 재시도 (사용자에게 비노출)
      if (retryCount < MAX_RETRY) {
        const next = await retryPayment(paymentState.orderId, paymentState.paymentKey)
        handlePaymentResponse(next, retryCount + 1)
      } else {
        // 재시도 소진 — UNKNOWN이면 마이페이지 확인 안내, 그 외 실패 안내
        showExhaustedGuide(response.code)
      }
      break
  }
}
```

---

## 상태 전이 요약

### OrderStatus

```
ORDERED ──────────────────────────────────► PAYMENT_PENDING
                  (PayPreprocessor)                │
                                          ┌────────┴──────────┐
                                     성공 ▼              실패 ▼
                                        PAID              FAIL
                                                           │
                                          자동 재시도 성공  │
                                          └──► PAYMENT_PENDING
```

> UNKNOWN은 OrderStatus를 PAYMENT_PENDING으로 유지한다. (FAIL로 내려가지 않음)

### PayStatus

```
PENDING ─────────────────────────────────► COMPLETE ──► REFUND
   │
   ├──► BUSINESS_FAILED    프론트: 실패 안내 + PG 위젯 재호출 버튼
   │
   ├──► SERVER_FAILED       프론트: "결제 처리 중" 유지 + 자동 재시도
   │         │                      재시도 소진 시 실패 안내
   │         └──(자동 재시도)──► PENDING
   │
   └──► UNKNOWN             프론트: "결제 처리 중" 유지 + 자동 재시도
             │                      재시도 소진 시 마이페이지 확인 안내
             └──(자동 재시도)──► PENDING
```
