# NBE10-12-1-T1

# ☕ 커피 주문 서비스

> 데브코스 백엔드 10기 12회차 1차 프로젝트 — 1팀 T1
> 

커피 원두 상품을 조회하고 주문할 수 있는 쇼핑몰 서비스입니다.
사용자는 상품 목록을 보고 주문을 생성할 수 있으며, 관리자는 상품 관리 및 전체 주문 내역을 조회할 수 있습니다.
오후 2시를 기준으로 같은 이메일 + 주소의 주문은 동일 배송으로 묶이고, Spring Batch + Scheduler를 통해 주문 상태가 자동으로 변경됩니다.

---

## 👥 팀원

| 이름 | 담당 |
| --- | --- |
| 이호영 [팀장] | 백엔드, 문서작성, 발표 |
| 김다훈 | 백엔드 |
| 양상훈 | 백엔드 |
| 여준 | 백엔드 |
| 조성호 | 백엔드, 프론트엔드 |

---

## 🛠 기술 스택

### Backend

- Java 25
- Spring Boot 4.0.6
- Spring Data JPA (Hibernate)
- Spring Batch + `@EnableScheduling`
- Spring Validation
- Lombok
- Springdoc OpenAPI (Swagger UI) 3.0.2
- H2 (개발 환경), MySQL (운영 환경)

### Frontend

- Next.js 16 (App Router)
- React 19
- TypeScript
- Tailwind CSS v4

---

## 📁 프로젝트 구조

```
NBE10-12-1-T1/
├── back/
│   └── src/main/java/com/back/
│       ├── domain/
│       │   ├── order/
│       │   │   ├── controller/   # OrderController
│       │   │   ├── dto/          # OrderRequestDto, OrderResponseDto, OrderItemResponseDto
│       │   │   ├── entity/       # Order, OrderItem
│       │   │   ├── enums/        # OrderStatus
│       │   │   ├── repository/   # OrderRepository
│       │   │   └── service/      # OrderService
│       │   └── product/
│       │       ├── controller/   # ProductController, AdminProductController
│       │       ├── dto/          # ProductRequestDto, ProductResponseDto
│       │       ├── entity/       # Product
│       │       ├── repository/   # ProductRepository
│       │       └── service/      # ProductService
│       └── global/
│           ├── annotation/       # @ApiV1
│           ├── batch/            # Spring Batch 설정
│           ├── config/           # WebConfig (CORS, 경로 설정)
│           ├── dto/              # ResponseDto
│           ├── exception/        # GlobalExceptionHandler, 커스텀 예외
│           └── jpa/entity/       # BaseEntity
└── front/
    └── src/
        ├── app/
        ├── components/
        │   ├── OrderPage.tsx       # 메인 페이지
        │   ├── ProductList.tsx     # 상품 목록
        │   ├── OrderSummary.tsx    # 주문 요약
        │   ├── LoginModal.tsx      # 관리자 로그인 모달
        │   ├── ProductFormModal.tsx # 상품 등록/수정 모달
        │   └── AdminOrderView.tsx  # 관리자 주문 목록
        └── types/
            └── order.ts
```

---

## ⚙️ 주요 기능

### 사용자

- 커피 원두 상품 목록 조회 (소프트 삭제된 상품 제외)
- 이메일 / 주소 입력 후 주문 생성
- 주문 시 상품 재고 자동 차감

### 관리자

- 로그인 (ID: admin / PW: admin, 프론트 하드코딩)
- 상품 추가 / 수정 (PATCH, 부분 수정 지원) / 소프트 삭제
- 전체 주문 목록 조회
- 주문별 주문 아이템 상세 조회

### 배송 묶음 처리

- 당일 오후 2시 이전 주문: 전일 오후 2시 이후 주문들과 묶임
- 당일 오후 2시 이후 주문: 익일 오후 2시 이전 주문들과 묶임
- 동일 이메일 + 주소 + 시간대 조건 일치 시 같은 `deliveryId` 부여

### 배치 처리

- Spring Batch + `@EnableScheduling`으로 주문 상태 자동 변경
- `OrderStatus` 흐름: `PAYMENT_COMPLETE(결제 완료)` → `PREPARING_PRODUCT(상품 준비 중)` → `IN_TRANSIT(배송 중)` → `DELIVERED(배송 완료)`
- 현재 구현 범위: `PAYMENT_COMPLETE(결제 완료)` → `PREPARING_PRODUCT(상품 준비 중)` 단계까지 동작, 이후 단계는 추후 구현 예정

---

## 📡 API 명세

베이스 URL: `http://localhost:8080/api/v1`

| Method | Endpoint | 설명 | 비고 |
| --- | --- | --- | --- |
| GET | `/products` | 상품 목록 조회 | 삭제되지 않은 상품만 |
| POST | `/orders` | 주문 생성 | 재고 차감, 배송 묶음 처리 |
| GET | `/admin/orders` | 관리자 주문 목록 조회 | 전체 주문 조회 |
| GET | `/admin/orders/{id}/order-items` | 관리자 주문 아이템 목록 조회 | 주문 상세 아이템 |
| GET | `/admin/products` | 관리자 상품 목록 조회 | 상품 전체 조회 |
| POST | `/admin/products` | 관리자 상품 생성 | 새로운 상품 추가 |
| PATCH | `/admin/products/{id}` | 관리자 상품 부분 수정 | 상품명, 상품 가격, 재고 수정 |
| DELETE | `/admin/products/{id}` | 관리자 상품 삭제 | 소프트 삭제 |

> Swagger UI: `http://localhost:8080/swagger-ui/index.html`
> 

### 응답 형식

```json
{
  "resultCode": "200-1",
  "message": "주문 목록 조회 성공",
  "data": { ... }
}
```

### 에러 코드

| 코드 | 상황 |
| --- | --- |
| `400-1` | 입력값 유효성 검사 실패 |
| `400-2` | 주문 상품 목록에 중복 상품 존재 |
| `404-1` | 상품을 찾을 수 없음 |
| `404-2` | 주문을 찾을 수 없음 |
| `409-1` | 재고 부족 |

---

## 🚀 실행 방법

### 사전 요구사항

- Java 25
- Node.js 18+

### 백엔드 실행

```bash
cd back
./gradlew bootRun
```

- 기본 포트: `8080`
- 기본 프로파일: `dev` (H2 파일 DB 사용)
- H2 콘솔: `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:./db_dev`
    - Username: `sa` / Password: (없음)

### 프론트엔드 실행

```bash
cd front
npm install
npm run dev
```

- 기본 포트: `3000`
- 백엔드 연결: `http://localhost:8080`

### Git Hook 설정

```bash
cd back
./gradlew build
```

---
## 🤝 협업 규칙

### 브랜치 전략

- `main` ← `feat/*`

### PR 규칙

- 모든 테스트 통과 여부 확인 후 PR 생성
- 수정 사항 및 중점 리뷰 포인트 기재
- Notion WBS 작업과 연결

---
## 🗂 ERD

> 
> 
> 
> <img width="701" height="271" alt="ERD" src="https://github.com/user-attachments/assets/1a507af6-8637-4ab3-a06f-04670e63ebfc" />
> 

---

## 🗂 UseCase

> 
> 
> 
> <img width="683" height="471" alt="UseCase" src="https://github.com/user-attachments/assets/17175385-9b9b-4d5d-9df6-c469bed90290" width="200" height="100"/>
> 
