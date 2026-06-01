# 🏢 AMS — 가맹점 자산 관리 시스템

> **Asset Management System** — 본사·협력사·가맹점 간 구매, 입고, 설치, 반품/회수를 통합 관리하는 전사 물류 플랫폼

---

## 📌 프로젝트 개요

AMS는 프랜차이즈 기업 **(주)중앙컴퍼니**가 운영하는 **가맹점 자산 관리 시스템**입니다.  
본사 직원이 가맹점에 필요한 자산(POS기, 냉장고 등)을 협력업체로부터 구매하고, 입고·설치·반품 과정 전반을 디지털화하여 실시간으로 추적할 수 있도록 설계되었습니다.

### 해결하는 문제

| 문제 | 해결책 |
|------|--------|
| 본사-협력사-가맹점 간 수작업 업무 처리 | 단일 플랫폼에서 요청→승인→완료 워크플로 자동화 |
| 재고 위치(창고 내 로케이션) 파악 불가 | 창고 Row/Col/Shelf 단위 물품 추적 |
| 물품 입출고 이력 관리 어려움 | 시리얼 번호 기반 전 생애주기 추적 |
| 다중 권한자 간 승인 체계 부재 | BIZ/CUS 역할 기반 단계별 승인 흐름 |

---

## ✨ 주요 기능

### 기준정보 관리
- 사업장·부서 관리
- 직원 등록·수정·비활성화
- 가맹점 등록 및 관리
- 협력업체(공급사) 관리
- 품목(자산) 등록 및 가격 관리
- 창고 등록 및 로케이션(행/열/단) 관리
- 공통 코드 관리

### 구매/설치 관리
- **구매 요청** — 본사 직원이 협력업체에 품목 구매 요청
- **구매 승인** — 협력업체 직원이 승인 또는 반려
- **입고 관리** — 승인된 구매 건 실물 입고 처리 (시리얼 번호 자동 생성)
- **설치 요청** — 가맹점 설치를 위한 출고 요청
- **설치 승인·완료** — 협력업체 승인 → 기사 설치 완료 처리
- **반품/회수 관리** — 가맹점 물품 회수 요청 및 재입고

### 물류 관리
- **물품 입출내역** — 전체 입출고 이력 조회 (시리얼 번호 기반)
- **재고 실사** — 창고별 전산 수량 vs 실제 수량 비교 및 반영
- **위치별 재고 현황** — 창고·로케이션별 현재 재고 현황 조회

---

## 🛠 기술 스택

### Backend

| 분류 | 기술 | 버전 |
|------|------|------|
| Language | Java | 21 (Preview 활성화) |
| Framework | Spring Boot | 3.4.1 |
| ORM | MyBatis | 3.0.4 |
| Security | Spring Security + JWT (RSA) | OAuth2 Resource Server |
| Database | MariaDB | - |
| Cloud | AWS S3 SDK | 2.29.17 |
| Build | Gradle | 8.11.1 |
| Utility | Lombok | 1.18.36 |

### Frontend

| 분류 | 기술 | 버전 |
|------|------|------|
| Framework | React | 18.3.1 |
| Build Tool | Vite | 6.0.5 |
| UI Library | Chakra UI | v3.2.5 |
| HTTP Client | Axios | 1.7.9 |
| Routing | React Router DOM | 6.28.1 |
| Select | React Select | 5.9.0 |
| Charts | Recharts | 2.15.1 |
| Auth | JWT Decode | 4.0.0 |
| Date | date-fns | 4.1.0 |

### Infrastructure

| 분류 | 기술 |
|------|------|
| Container | Docker |
| Cloud Server | AWS EC2 |
| Storage | AWS S3 |
| Database | MariaDB (AWS 또는 EC2 내 — 확인 필요) |
| Reverse Proxy | 없음 (직접 포트 8080 노출) |

---

## 🏗 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                      Client (Browser)                   │
│                    React + Chakra UI                    │
│              Vite Dev Server (Port 5173)                │
└──────────────────────────┬──────────────────────────────┘
                           │ HTTP (Axios)
                           │ Authorization: Bearer <JWT>
┌──────────────────────────▼──────────────────────────────┐
│               Spring Boot Application                   │
│                    (Port 8080)                          │
│                                                         │
│  ┌──────────────┐   ┌──────────────┐  ┌─────────────┐  │
│  │  Controllers │ → │   Services   │→ │   MyBatis   │  │
│  │  (REST API)  │   │ (Biz Logic)  │  │   Mappers   │  │
│  └──────────────┘   └──────────────┘  └──────┬──────┘  │
│                                               │         │
│  ┌──────────────────────────────────────────┐ │         │
│  │  Spring Security (JWT RSA Verification)  │ │         │
│  └──────────────────────────────────────────┘ │         │
└──────────────────────────────────────────┬────┘─────────┘
                                           │
                          ┌────────────────▼───────────┐
                          │         MariaDB             │
                          │   (23개 테이블 + 1 View)    │
                          └────────────────────────────┘
                                           
┌──────────────────────────────────────────────────────────┐
│                     Docker Container                     │
│  (Production: AWS EC2 52.79.239.190, Port 8080)          │
│  Static Files served by Spring Boot (frontend build)    │
└──────────────────────────────────────────────────────────┘
```

### 인증 흐름

```
Client                    Spring Boot               DB
  │                           │                     │
  │── POST /api/login/siteIn ─▶│                     │
  │    {employeeNo, password}  │── SELECT TB_EMPMST ─▶│
  │                            │◀──── employee ──────│
  │                            │                     │
  │                            │  RSA 서명으로 JWT 생성│
  │                            │  (scope: BIZ/CUS)   │
  │◀── {token, name, company} ─│                     │
  │                            │                     │
  │── GET /api/... ────────────▶│                     │
  │   Authorization: Bearer JWT│  JWT 서명 검증       │
  │                            │  scope 기반 권한 확인 │
  │◀── Response ───────────────│                     │
```

---

## 📁 프로젝트 구조

```
AMSprj0106/
├── backend/                          # Spring Boot 백엔드
│   ├── src/main/java/com/example/backend/
│   │   ├── BackendApplication.java   # 애플리케이션 진입점
│   │   ├── config/
│   │   │   └── AppConfiguration.java # JWT, S3, Security 설정
│   │   ├── controller/
│   │   │   ├── standard/             # 기준정보 컨트롤러
│   │   │   │   ├── business/         # 사업장 관리
│   │   │   │   ├── commonCode/       # 공통 코드
│   │   │   │   ├── customer/         # 협력업체
│   │   │   │   ├── department/       # 부서
│   │   │   │   ├── employee/         # 직원
│   │   │   │   ├── franchise/        # 가맹점
│   │   │   │   ├── item/             # 품목
│   │   │   │   ├── location/         # 로케이션
│   │   │   │   ├── login/            # 로그인
│   │   │   │   ├── main/             # 메인 대시보드
│   │   │   │   ├── member/           # 내 정보
│   │   │   │   └── warehouse/        # 창고
│   │   │   ├── state/                # 구매/설치 컨트롤러
│   │   │   │   ├── install/          # 설치 관리
│   │   │   │   ├── instk/            # 입고 관리
│   │   │   │   ├── purchase/         # 구매 관리
│   │   │   │   └── retrieve/         # 반품/회수
│   │   │   └── stock/                # 물류 컨트롤러
│   │   │       ├── inoutHistory/     # 입출내역
│   │   │       └── stocktaking/      # 재고 실사 + 현황
│   │   ├── dto/                      # 데이터 전송 객체
│   │   │   ├── standard/             # 기준정보 DTO
│   │   │   ├── state/                # 구매/설치 DTO
│   │   │   └── stock/                # 물류 DTO
│   │   ├── mapper/                   # MyBatis 인터페이스 (SQL 포함)
│   │   │   ├── standard/
│   │   │   ├── state/
│   │   │   └── stock/
│   │   └── service/                  # 비즈니스 로직
│   │       ├── standard/
│   │       ├── state/
│   │       └── stock/
│   ├── src/main/resources/
│   │   ├── application.properties    # 앱 설정
│   │   └── secret/                   # ⚠️ .gitignore 제외 (직접 생성 필요)
│   │       ├── app.pub               # JWT 공개키 (RSA)
│   │       ├── app.key               # JWT 개인키 (RSA)
│   │       └── custom.properties     # DB, AWS 설정
│   ├── Dockerfile
│   ├── build.sh                      # 빌드 + 배포 스크립트
│   ├── run.sh                        # Docker 실행 스크립트
│   └── build.gradle
│
├── frontend/                         # React 프론트엔드
│   ├── src/
│   │   ├── App.jsx                   # 라우터 설정
│   │   ├── main.jsx                  # 앱 진입점
│   │   ├── context/
│   │   │   └── AuthenticationProvider.jsx  # 전역 인증 상태
│   │   ├── components/
│   │   │   ├── standard/             # 기준정보 UI 컴포넌트
│   │   │   ├── state/                # 구매/설치 UI 컴포넌트
│   │   │   ├── stock/                # 물류 UI 컴포넌트
│   │   │   ├── tool/                 # 공통 도구 (검색, 정렬, 페이지네이션)
│   │   │   └── ui/                   # Chakra UI 래퍼 컴포넌트
│   │   └── page/                     # 페이지 컴포넌트
│   │       ├── login/
│   │       ├── main/
│   │       ├── memberInfo/
│   │       ├── standard/
│   │       ├── state/
│   │       └── stock/
│   ├── package.json
│   ├── vite.config.js
│   └── index.html
│
└── sql/                              # DDL 및 초기 데이터
    ├── standard/                     # 기준정보 테이블 DDL
    └── state/                        # 구매/설치 테이블 DDL
    └── stock/                        # 물류 테이블 DDL
```

---

## 👥 사용자 권한 체계

### 역할 정의

| 역할 코드 | 설명 | JWT scope |
|-----------|------|-----------|
| `BIZ` | 본사 관리자 (최고 권한) | `BIZ` |
| `EMP` | 본사 부서 직원 | `BIZ` (동일 처리) |
| `CUS` | 협력업체 직원 | `CUS` |

> **직원 번호 형식**  
> - 본사 관리자: `BIZEMP000000X`  
> - 본사 부서 직원: `{부서코드}EMP000000X`  
> - 협력업체 직원: `CUSEMP000000X`

### 기능별 권한 매트릭스

| 기능 | BIZ/EMP | CUS |
|------|:-------:|:---:|
| 기준정보 CRUD (전체) | ✅ | ❌ |
| 로케이션 조회 | ✅ | ✅ (자사 창고만) |
| 구매 요청 | ✅ | ❌ |
| 구매 승인/반려 | ✅ | ✅ (담당 품목만) |
| 설치 요청 | ✅ | ❌ |
| 설치 승인/반려 | ✅ | ✅ (담당 품목만) |
| 설치 완료 처리 | ✅ | ✅ (담당 기사만) |
| 입고 승인 | ✅ | ✅ (자사 입고만) |
| 반품 요청 | ✅ | ❌ |
| 반품 승인/반려 | ✅ | ✅ (담당 품목만) |
| 재고 실사 등록 | ✅ | ✅ (자사 창고만) |
| 전체 현황 조회 | ✅ | ❌ (자사만 조회) |

---

## 🔄 주요 업무 프로세스

### 1. 구매 → 입고 프로세스

```
본사 직원                협력업체 직원              시스템
    │                        │                      │
    │ 구매 요청              │                      │
    │ POST /api/purchase/request                    │
    │──────────────────────────────────────────────▶│
    │                        │                      │ TB_PURCH_REQ 생성
    │                        │ 구매 승인             │ (consent=NULL)
    │                        │ POST /api/purchase/approve/{key}
    │                        │─────────────────────▶│
    │                        │                      │ TB_PURCH_APPR 생성
    │                        │                      │ TB_BUYIN 생성 (가입고)
    │                        │                      │ 발주번호 PUR001 생성
    │                        │                      │
    │                        │ 실물 입고 승인         │
    │                        │ POST /api/instk/add  │
    │                        │─────────────────────▶│
    │                        │                      │ 시리얼 번호 자동 생성 (S00001~)
    │                        │                      │ TB_ITEMSUB 등록 (current=WHS)
    │                        │                      │ TB_INSTK_SUB 로케이션 배정
    │                        │                      │ TB_INOUT_HIS 입고 이력 기록
```

### 2. 설치 프로세스

```
본사 직원              협력업체 직원            설치 기사
    │                      │                      │
    │ 설치 요청             │                      │
    │ POST /api/install/request                    │
    │─────────────────────▶│                      │
    │                      │                      │
    │                      │ 설치 승인             │
    │                      │ POST /api/install/approve
    │                      │─────────────────────▶(시스템)
    │                      │                      │ 출고번호 OUT001 생성
    │                      │                      │ 재고에서 시리얼 번호 할당
    │                      │                      │ item_sub_active = 0
    │                      │                      │
    │                      │               설치 완료 처리
    │                      │               POST /api/install/configuration
    │                      │                      │──▶(시스템)
    │                      │                      │   current=FRN(가맹점)
    │                      │                      │   TB_INOUT_HIS OUT 기록
```

### 3. 반품/회수 프로세스

```
본사 직원              협력업체 직원
    │                      │
    │ 반품 요청 (시리얼 번호 선택)
    │ POST /api/return/request
    │─────────────────────▶│
    │                      │
    │                      │ 반품 승인 (검수기사 배정, 회수 예정일 설정)
    │                      │ POST /api/return/approve
    │                      │──▶(시스템)
    │                      │   발번호 RTN001 생성
    │                      │   TB_BUYIN 가입고 등록
    │                      │
    │                      │ 실물 회수 후 입고 승인
    │                      │ POST /api/instk/add (RETRN)
    │                      │──▶(시스템)
    │                      │   current=WHS (창고 복귀)
    │                      │   TB_INOUT_HIS RETRN 기록
```

---

## 🗄 데이터베이스 구조

### 전체 테이블 목록

#### 기준정보 (Standard)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `TB_BIZMST` | 본사 정보 | business_code, business_name |
| `TB_DEPARTMST` | 부서 정보 | department_code, department_name |
| `TB_SYSCOMM` | 공통 코드 | common_code(PK), common_code_type(ITEM/STATE/STANDARD) |
| `TB_CUSTMST` | 협력업체 | customer_code, item_code(취급품목) |
| `TB_EMPMST` | 직원 | employee_no, employee_common_code(BIZ/CUS/EMP) |
| `TB_FRNCHSMST` | 가맹점 | franchise_code, franchise_name |
| `TB_ITEMMST` | 품목 마스터 | item_common_code, input_price, output_price |
| `TB_ITEMSUB` | 품목 개별 (시리얼) | serial_no, current_common_code(WHS/FRN/LOS) |
| `TB_WHMST` | 창고 | warehouse_code, customer_code |
| `TB_LOCMST` | 로케이션 | row, col, shelf, located(재고여부) |

#### 구매/설치 (State)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `TB_PURCH_REQ` | 구매 요청 | purchase_consent(NULL/T/F) |
| `TB_PURCH_APPR` | 구매 승인 | purchase_no(PUR001), warehouse_code |
| `TB_BUYIN` | 가입고 | input_common_code(INSTK/RETRN), input_consent |
| `TB_INSTK` | 입고 확정 | input_stock_date |
| `TB_INSTK_SUB` | 입고 상세 (시리얼+로케이션) | serial_no, location_key |
| `TB_INSTL_REQ` | 설치 요청 | install_request_consent |
| `TB_INSTL_APPR` | 설치 승인 | output_no(OUT001), install_approve_consent |
| `TB_INSTL_SUB` | 설치 상세 (출고 시리얼) | output_no, serial_no |
| `TB_RTN_REQ` | 반품 요청 | return_consent |
| `TB_RTN_APPR` | 반품 승인 | return_no(RTN001), return_date |
| `TB_DISPR` | 반려 이력 | state_common_code(PURCH/INSTL/INSTK/RETRN) |

#### 물류 (Stock)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `TB_INOUT_HIS` | 입출고 전체 이력 | inout_common_code(INSTK/RETRN/OUT/LOS/STKP) |
| `TB_STKTK` | 재고 실사 | count_current, count_configuration, count_difference(자동계산) |
| `V_ITEM_CRNT` | 현재 재고 현황 (View) | 창고별·품목별 현재 재고 수량 집계 |

### 물품 생애주기

```
구매 승인 → 입고 → 창고 배치 → 설치 출고 → 반품 회수 → 재입고 / 분실
  (없음)    S00001  current=WHS  current=FRN  current=WHS  current=LOS
```

---

## 🌐 API 구조

### 인증

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/api/login/siteIn` | 로그인 (JWT 발급) | 없음 |

### 기준정보 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/api/business/view` | 사업장 정보 조회 | - |
| PUT | `/api/business/update` | 사업장 정보 수정 | BIZ |
| GET/POST/PUT | `/api/department/**` | 부서 CRUD | BIZ |
| GET/POST/PUT | `/api/commonCode/**` | 공통 코드 CRUD | BIZ |
| GET/POST/PUT | `/api/employee/**` | 직원 CRUD | BIZ |
| GET/POST/PUT | `/api/customer/**` | 협력업체 CRUD | BIZ |
| GET/POST/PUT | `/api/franchise/**` | 가맹점 CRUD | BIZ |
| GET/POST/PUT | `/api/item/**` | 품목 CRUD | BIZ |
| GET/POST/PUT | `/api/warehouse/**` | 창고 CRUD | BIZ |
| GET/POST/PUT | `/api/location/**` | 로케이션 CRUD | 인증 필요 |

### 구매/설치 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/api/purchase/request` | 구매 요청 | BIZ |
| GET | `/api/purchase/list` | 구매 목록 | 인증 필요 |
| POST | `/api/purchase/approve/{key}` | 구매 승인 | BIZ 또는 담당 CUS |
| POST | `/api/purchase/disapprove` | 구매 반려 | BIZ 또는 담당 CUS |
| POST | `/api/instk/add` | 입고 승인 | 인증 필요 |
| PUT | `/api/instk/reject` | 입고 반려 | 인증 필요 |
| GET | `/api/instk/list` | 입고 목록 | 인증 필요 |
| POST | `/api/install/request` | 설치 요청 | BIZ |
| POST | `/api/install/approve` | 설치 승인 | BIZ 또는 담당 CUS |
| POST | `/api/install/configuration` | 설치 완료 | BIZ 또는 담당 기사 |
| POST | `/api/install/disapprove` | 설치 반려 | BIZ 또는 담당 CUS |
| POST | `/api/return/request` | 반품 요청 | BIZ |
| POST | `/api/return/approve` | 반품 승인 | BIZ 또는 담당 CUS |
| POST | `/api/return/disapprove` | 반품 반려 | BIZ 또는 담당 CUS |

### 물류 API

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| GET | `/api/inoutHistory/list` | 입출내역 목록 | 인증 필요 |
| GET | `/api/inoutHistory/view/{key}` | 입출내역 상세 | 인증 필요 |
| GET | `/api/stocktaking/list` | 재고 실사 목록 | 인증 필요 |
| POST | `/api/stocktaking/add` | 재고 실사 등록 | 인증 필요 |
| POST | `/api/stocktaking/updateStock` | 실사 반영 | 인증 필요 |
| GET | `/api/inventory/list` | 위치별 재고 현황 | 인증 필요 |

### 공통 응답 형식

```json
// 성공
{
  "message": {
    "type": "success",
    "text": "저장되었습니다."
  }
}

// 경고
{
  "message": {
    "type": "warning",
    "text": "중복된 항목이 존재합니다."
  }
}

// 오류
{
  "message": {
    "type": "error",
    "text": "필수 항목이 입력되지 않았습니다."
  }
}
```

---

## 🖥 화면 구성

### 사이드바 구조

**기준정보 관리** (`/business`, `/employee`, `/franchise`, ...)
```
├── 사업장/부서 관리
├── 인사 관리
├── 가맹점 관리
├── 협력 업체 관리
├── 품목 관리
├── 창고 관리
├── 로케이션 관리    ← CUS 직원도 접근 가능 (자사 창고만)
└── 공통 코드 관리
```

**구매/설치 관리** (`/purchase`, `/instk`, ...)
```
├── 구매 관리
├── 입고 관리
├── 설치 관리
└── 반품/회수 관리
```

**물류 관리** (`/inoutHistory`, `/stocktaking`, ...)
```
├── 물품 입출내역
├── 재고 실사
└── 위치별 재고 현황
```

### 공통 UI 패턴

- 모든 목록 화면: 검색(타입 + 키워드) + 정렬(컬럼 클릭) + 페이지네이션(10건)
- 상세/수정: 더블 클릭 → 다이얼로그(팝업)
- 토스트 알림: 성공(success) / 경고(warning) / 오류(error)

---

## 🔧 개발 가이드

### 새 API 추가 절차

1. **DTO 생성** → `dto/{domain}/{DomainName}.java`
2. **Mapper 인터페이스 생성** → `mapper/{domain}/{DomainName}Mapper.java`  
   (SQL은 `@Select`, `@Insert` 등 애노테이션으로 직접 작성)
3. **Service 생성** → `service/{domain}/{DomainName}Service.java`
4. **Controller 생성** → `controller/{domain}/{DomainName}Controller.java`
5. **프론트엔드 컴포넌트** → `frontend/src/components/{domain}/`
6. **페이지 라우트 추가** → `frontend/src/App.jsx`

### 코드 컨벤션

```java
// Controller - ResponseEntity 반환 패턴
@PostMapping("add")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<Map<String, Object>> add(@RequestBody Entity entity, Authentication auth) {
    if (!service.validate(entity)) {
        return ResponseEntity.badRequest().body(Map.of(
            "message", Map.of("type", "error", "text", "필수 항목이 입력되지 않았습니다.")
        ));
    }
    if (service.add(entity)) {
        return ResponseEntity.ok().body(Map.of(
            "message", Map.of("type", "success", "text", "등록되었습니다.")
        ));
    }
    return ResponseEntity.internalServerError().body(Map.of(
        "message", Map.of("type", "error", "text", "등록에 실패하였습니다.")
    ));
}
```

```javascript
// Frontend - Axios + toaster 패턴
axios.post('/api/{domain}/add', data)
  .then(res => {
    toaster.create({ type: res.data.message.type, description: res.data.message.text });
    onClose(); // 다이얼로그 닫기
    refresh(); // 목록 새로고침
  })
  .catch(e => {
    const message = e.response?.data?.message;
    toaster.create({ type: message.type, description: message.text });
  });
```

### SQL 작성 규칙 (MyBatis)

```java
// 동적 쿼리는 @Select 내 <script> 태그 활용
@Select("""
    <script>
    SELECT *
    FROM TB_EXAMPLE
    WHERE 1=1
    <if test="keyword != null and keyword.trim() != ''">
        AND name LIKE CONCAT('%', #{keyword}, '%')
    </if>
    ORDER BY ${sort} ${order}
    LIMIT #{offset}, 10
    </script>
    """)
List<Example> list(int offset, String keyword, String sort, String order);
```

> ⚠️ `${sort}`, `${order}` 등 `${}` 사용 시 SQL 인젝션 위험이 있습니다.  
> 컨트롤러/서비스에서 허용된 컬럼명만 받도록 화이트리스트 검증이 필요합니다.

---

## ❓ FAQ

**Q. 로그인이 안 됩니다.**  
A. `secret/app.pub`, `secret/app.key` 파일이 올바른 RSA 형식인지 확인하세요. `custom.properties`의 DB 연결 정보가 올바른지도 확인하세요.

**Q. JWT 토큰은 언제 만료됩니까?**  
A. 로그인 후 **24시간**이 지나면 자동으로 로그아웃됩니다. (`LoginService.java` 기준)

**Q. 협력업체 직원(CUS)이 구매 요청을 할 수 없습니다.**  
A. 의도된 동작입니다. 구매 요청은 본사(BIZ/EMP) 직원만 가능합니다.

**Q. 시리얼 번호는 어떻게 생성됩니까?**  
A. 입고 승인 시 `S + 5자리 숫자` 형식으로 자동 생성됩니다. (예: `S00001`, `S00002`)

**Q. 로케이션 없이 입고할 수 있습니까?**  
A. 창고 내 빈 로케이션(located=false)이 자동 배정됩니다. 빈 로케이션이 없으면 입고가 실패합니다.

---

## 🔮 향후 개선 사항

코드 분석을 기반으로 확인된 개선 포인트입니다.

| 영역 | 현황 | 개선 방향 |
|------|------|-----------|
| SQL 인젝션 방어 | `${sort}`, `${order}` 직접 삽입 | 화이트리스트 검증 레이어 추가 |
| 테스트 코드 | 거의 없음 | JUnit 단위 테스트 + Testcontainers 통합 테스트 |
| API 문서화 | 없음 | Swagger / SpringDoc OpenAPI 도입 |
| 에러 핸들링 | 컨트롤러별 분산 | `@RestControllerAdvice` 전역 핸들러 통합 |
| 환경 설정 | 하드코딩된 IP/경로 | 환경 변수 또는 외부 설정 파일 분리 |
| 파일 업로드 | S3 설정만 있고 실제 사용 미확인 | 프로필 이미지 등 실제 활용 구현 |
| 페이지당 건수 | 10건 고정 | 사용자 선택 가능하도록 개선 |
| 로깅 | 기본 `System.out.println` | Logback/SLF4J 구조화 로깅 도입 |
| HTTPS | HTTP 직접 노출 | HTTPS + 리버스 프록시 (Nginx) 적용 |

---

### 커밋 메시지 컨벤션

```
feat: 새 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷 변경 (로직 변경 없음)
refactor: 코드 리팩토링
test: 테스트 추가/수정
chore: 빌드 스크립트 등 기타 변경
```
