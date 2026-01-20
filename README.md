# 📊 Database & Backend (PostgreSQL, Supabase)

**데이터베이스 설계 및 n8n 연동 담당**

- DB 스키마 설계
- Supabase ↔ n8n (Postgres Node) 연결 설정 및 테스트
- SQL 쿼리를 이용한 데이터 검증 및 디버깅 워크플로 구성

<br/>

## 기술 선택 이유

금융 기사 데이터, AI 요약 결과, 문제 생성 결과를 구조적으로 저장하고 관리할 수 있는 데이터베이스가 필요

→ PostgreSQL 기반의 Supabase를 데이터 저장소로 선택

<br/>

### PostgreSQL 선택 이유

관계형 데이터베이스로서 기사 → 실행 로그 → 퀴즈 간의 명확한 관계(FK)를 설계하기에 적합

JSONB 타입을 통해 AI가 생성한 문제 데이터를 유연하게 저장 가능

트랜잭션, 인덱스, 제약조건 등 데이터 무결성과 확장성 확보 가능

<br/>

|  | MySQL | PostgreSQL |
| --- | --- | --- |
| **구분** | RDBMS | ORDBMS |
| **데이터 타입 확장성** | JSON 지원은 있으나 기능 제한적 | JSONB, ENUM, ARRAY 등 고급 타입 지원 |
| **JSON 데이터 처리** | JSON 내부 필드 직접 쿼리,인덱싱 가능 | 상대적으로 제약 조건 활용 제한 |
| **Supabase 호환성** | Supabase 기본 DB | Supabase 미지원 |

<br/>

### Supabase 선택 이유

PostgreSQL을 기반으로 한 관리형 DB 서비스로 빠른 프로젝트 세팅 가능

SQL Editor를 통한 직접적인 스키마 관리 및 디버깅 용이

n8n과의 연동 시 Postgres Pooler 연결을 지원하여 안정적인 자동화 환경 구축 가능

<br/>

### 데이터 베이스 구조 요약

- execution_logs
    - 자동화 파이프라인의 실행 상태(SUCCESS / FAILED / RUNNING) 기록
- articles
    - 크롤링된 금융 기사 원문과 AI 요약 데이터 저장
- quizzes
    - 기사별로 생성된 금융 시사 문제 세트(JSON 형태) 저장

기사 수집 → 요약 → 문제 생성 → PDF 출력으로 이어지는 전체 자동화 흐름을 안정적으로 추적하고 관리할 수 있도록 데이터 구조 설계

<br/>

## n8n 연동 방식

- Docker 기반 n8n 환경에서 Postgres Node를 사용하여 Supabase DB에 직접 접근
- SELECT / INSERT/ JOIN 쿼리 활용해
    - 자동화 결과 검증
    - 실패 로그 추적
    - AI 생성 데이터 확인
