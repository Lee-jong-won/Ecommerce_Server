# 결제 정상 흐름

> 실패 및 재시도 흐름은 `payment-retry-flow.md` 참고

---

## 주문 프로세스

결제 시도 전, 반드시 주문이 먼저 생성되어야 한다. 주문 생성은 **장바구니 확정 시 단 한 번**만 호출한다.

```
[프론트]                              [서버]
  │                                    │
  │  사용자가 장바구니에서 주문 확정     │
  │                                    │
  ├─ POST /api/order ────────────────► │  상품 조회 (productId별 단가 확인)
  │  { orderName, cartLineItems }   │  주문 상품별 금액 계산
  │                                    │  orderId 생성 (서버에서 UUID 발급)
  │                                    │  Order 생성 (ORDERED)
  │◄──── 200 { orderId, totalAmount } ─┤  OrderItem 저장
  │                                    │
  │  orderId, totalAmount 저장          │
  │                                    │
  ├─ PG 위젯 호출                      │
  │  (orderId, totalAmount 전달)        │
  │◄─── paymentKey 발급 ──────────────┤ (PG사)
  │                                    │
  └─ 결제 승인 API 호출로 이어짐        │
```

**주의사항**
- orderId는 **서버에서 생성**한다. 프론트는 응답으로 받은 orderId를 저장해 이후 결제 API에 전달한다.
- totalAmount는 서버가 상품 단가를 기준으로 계산한 값이다. PG 위젯과 결제 API에 이 값을 그대로 사용해야 한다. 클라이언트가 임의로 변경하면 `PayPreprocessor`의 금액 검증에서 거부된다.

---

## API 명세

### 주문 생성

```
POST /api/order
Authorization: X-MOCK-USER-LOGINID: {loginId}
Content-Type: application/json

{
  "orderName": "상품명",
  "orderCreates": [
    { "productId": 1, "quantity": 2 }
  ]
}
```

```json
// 200 OK
{
  "orderId": "ORDER-abc123",   // 서버에서 생성한 UUID 기반 식별자
  "orderName": "상품명",
  "totalAmount": 15000         // 서버가 계산한 주문 금액
}
```

---

### 결제 승인

```
POST /api/payment
Authorization: X-MOCK-USER-LOGINID: {loginId}
Content-Type: application/json

{
  "paymentKey": "PG사가 발급한 키",
  "orderId": "ORDER-abc123",
  "pgType": "TOSS",
  "amount": 15000
}
```

```json
// 201 Created
{
  "payAmount": 15000,
  "orderName": "상품명",
  "approvedAt": "2026-05-06T10:00:00+09:00"
}
```

---

## 결제 API 내부 흐름

결제 API는 내부적으로 세 단계로 실행된다.

```
POST /api/payment
       │
       ▼
  [1] PayPreprocessor          주문 소유자 확인, 금액 검증
                               PayRequest 생성 (PENDING)
                               Order → PAYMENT_PENDING
       │
       ▼
  [2] PaymentExecutor          PG 승인 API 호출
       │
       ▼
  [3] PaySuccessProcessor      Pay 엔티티 생성
                               재고 차감
                               PayRequest → COMPLETE
                               Order → PAID
       │
       ▼
  201 Created
```

---

## 정상 결제 흐름

```
[프론트]                              [서버]
  │                                    │
  ├─ POST /api/order ────────────────► │  Order 생성 (ORDERED)
  │◄─────────────────── 200 orderId ──┤
  │                                    │
  ├─ PG 위젯 호출 (orderId 전달)       │
  │◄─── paymentKey 발급 ──────────────┤ (PG사)
  │                                    │
  ├─ POST /api/payment ──────────────► │  PayRequest 생성 (PENDING)
  │  { orderId, paymentKey, amount }   │  Order → PAYMENT_PENDING
  │                                    │  PG 승인 API 호출
  │                                    │  PayRequest → COMPLETE
  │◄──────────── 201 성공 응답 ────────┤  Order → PAID
  │                                    │
  └─ 결제 완료 화면                     │
```

**상태 변화**

| 시점 | OrderStatus | PayRequest |
|------|-------------|------------|
| 주문 생성 | ORDERED | — |
| 결제 API 진입 | PAYMENT_PENDING | PENDING |
| PG 승인 완료 | PAID | COMPLETE |
