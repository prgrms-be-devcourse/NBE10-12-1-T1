# Beantage 프론트엔드 작업 로그

## 1. 백엔드 API 연결

### 상품 조회
- 비로그인(유저): `GET /products` → 재고 미포함
- 관리자: `GET /admin/products` → 재고 포함

### 상품 CRUD (관리자)
- 추가: `POST /admin/products`
- 수정: `PATCH /admin/products/:id`
- 삭제: `DELETE /admin/products/:id`

### 로그인
- `POST /admin/login`
- request body: `{ id, password }` (기본값 admin/admin)
- 응답 200 OK → 관리자 모드 전환

### 응답 형식
백엔드는 `{ resultCode, message, data }` 형태로 응답.
JSON snake_case(`img_url`) → TypeScript camelCase(`imgUrl`) 변환은 `toProduct()` 함수에서 처리 (`OrderPage.tsx:13`).

---

## 2. 재고 분리 표시

- `Product` 타입에서 `stock` 필드를 `optional`로 선언 (`stock?: number`)
- 유저 화면: 재고 미표시
- 관리자 화면: `isAdmin && product.stock !== undefined` 조건으로 재고 표시
- 로그인 시 `/admin/products`로 재조회, 로그아웃 시 `/products`로 재조회

---

## 3. 카카오 우편번호 하단 바 마스킹

카카오 Postcode iframe은 `overflow: hidden`으로 클리핑 불가.
흰색(`#ffffff`) div를 iframe 위에 absolute로 덮어서 하단 130px 가림.
`pointerEvents` 미설정 → 마스크 영역 클릭 차단.

관련 코드: `OrderSummary.tsx` 내 showPostcode 모달 섹션

---

## 4. Git 인터랙티브 리베이스 충돌 해결

`feat/56/dev` 브랜치를 `f1e5f06`에 리베이스하는 과정에서 8개 커밋 전체 충돌 발생.
전략: HEAD(API 연동 코드) 기준으로 유지, 구 디자인 참조 코드 폐기.

충돌 해결 파일:
- `src/app/layout.tsx`
- `src/components/OrderPage.tsx`
- `src/components/OrderSummary.tsx`
- `src/components/ProductList.tsx`
- `src/components/AdminOrderView.tsx`
- `src/components/ProductFormModal.tsx`

---

## 5. 제거된 필드

구버전 코드에 있던 `origin` 필드는 백엔드에 없어 전면 제거.
- `Product` 타입에서 삭제
- `AdminOrderView`, `OrderSummary`, `ProductList` 등 참조 부분 → `product.name`으로 대체

---

## 파일별 주요 역할

| 파일 | 역할 |
|------|------|
| `src/components/OrderPage.tsx` | 메인 페이지, API 호출 전체, 상태 관리 |
| `src/components/ProductList.tsx` | 상품 목록, 관리자 수정/삭제 오버레이 |
| `src/components/OrderSummary.tsx` | 장바구니 dock 패널, 주문 폼, 카카오 주소 검색 |
| `src/components/LoginModal.tsx` | 관리자 로그인 (`POST /admin/login`) |
| `src/components/ProductFormModal.tsx` | 상품 추가/수정 폼 |
| `src/components/AdminOrderView.tsx` | 관리자 주문 내역 뷰 |
| `src/types/order.ts` | 공통 타입 정의 |
