## 1. 기획 배경

### 💭 Pain Point

> 금융/경제 기사들을 읽기만 하는 **'수동적 독서'의 휘발성**에 집중
> 
1. 사용자가 금융 기사를 입력하면
2. AI가 이를 분석해 굿노트용 학습지로 만들어주어
3. 사용자의 능동적 학습 유도

### **🎯 핵심 목표:**

1. **능동적 학습:** 요약뿐만 아니라 객관식/주관식 퀴즈를 통한 사고력을 확장
2. **자동화:** URL만 넣으면 PDF 생성부터 직접 굿노트 어플로 저장

---

## 2. 시스템 설계 및 아키텍처

### 🛠 Tech Stack

- **Orchestration:** n8n (워크플로우 자동화)
- **Database:** PostgreSQL (데이터 영구 저장 및 관리)
- **AI Engine:** OpenAI GPT-4.1-NANO (뉴스 요약 및 퀴즈 생성)
- **Frontend/Trigger:** HTML Dashboard (사용자 입력 및 제어)
- **PDF/Email:** Google Apps Script (HTML 템플릿 렌더링 및 메일 발송)

### 📊 데이터베이스 모델링 (PostgreSQL)
<img width="1402" height="899" alt="image" src="https://github.com/user-attachments/assets/82a8262d-bf74-43b7-9333-581279f132ee" />


관계형 DB를 사용하되 유연성을 위해 **JSONB** 타입을 활용

- **`articles`**: 기사 원문 및 요약 저장
- **`quizzes`**: 생성된 퀴즈 세트 저장.
    - `quiz_data` 컬럼을 **JSONB**로 설계하여, [객관식, 주관식, 정답, 해설]이 담긴 **배열(Array)** 전체를 하나의 행(Row)에 저장
- **`execution_logs`**: 실행 이력 및 상태 추적

---

## 3. 핵심 기능 구현

### 1️⃣ n8n

> 전체 워크플로우

<img width="2138" height="1006" alt="image" src="https://github.com/user-attachments/assets/73af6f6a-ccdb-44b0-b997-a34f6b8e4d0c" />


### A. 실시간 생성 모드 (URL Trigger)
<img width="2186" height="388" alt="image" src="https://github.com/user-attachments/assets/22a3f886-9594-4bf0-8eea-8ac3db9e0fd6" />


1. 사용자가 대시보드에서 뉴스 URL & 굿노트로 발송받을 이메일 주소를 입력
2. n8n이 기사 본문을 크롤링
3. OpenAI가 Markdown 형식으로 요약 및 퀴즈(객관식/주관식) 생성
4. 데이터를 데이터베이스에 적재
5. Google Apps Script(GAS)로 데이터 전송 → HTML 템플릿 렌더링 → PDF 변환 → 이메일 발송

### B. 랜덤 문제 생성 모드 (Random Trigger)
<img width="2554" height="330" alt="image" src="https://github.com/user-attachments/assets/49c1207e-b33e-429f-b3bd-cfa997536158" />


1. DB(`quizzes` + `articles` 조인)에서 `ORDER BY RANDOM() LIMIT 1` 쿼리로 과거 문제 1세트 추출
2. 저장된 JSON 데이터를 다시 Markdown 텍스트로 역직렬화
3. GAS로 전송하여 PDF 발송
4. 발송 완료된 문제는 데이터베이스에서 자동 삭제하여 중복 학습 방지

### C. 스케줄링을 통한 데이터 크롤링
<img width="2070" height="384" alt="image" src="https://github.com/user-attachments/assets/4db77f69-8f14-432f-bda0-693d0175128f" />


1. 하루 단위로 한국 경제 사이트의 금융 카테고리 기사를 크롤링
2. 기사 본문을 JSON 형태로 데이터베이스에 적재
3. OpenAI가 Markdown 형식으로 요약 및 퀴즈(객관식/주관식) 생성
4. 퀴즈 데이터를 데이터베이스에 적재
5. 임계치(5개)의 퀴즈가 추가적으로 생성되면 사용자에게 디스코드로 알림 전송

### 2️⃣ 사용자 대시보드

```bash
로컬 Python 서버 실행 명령어
>> python3 -m http.server 8000
```

<img width="2070" height="384" alt="image" src="https://github.com/user-attachments/assets/5c30f8d9-eef4-4774-89a2-2cf6b47f958b" />


- n8n Webhook URL을 연결해
    - 사용자가 버튼을 트리거 하면
    - 두 가지 모드 (url or 랜덤) 방식으로 분기된 상태를 n8n 노드에 전달

### 3️⃣ Google App Script

> **Code.gs**  (로직 처리)
> 
1. n8n으로부터 전달받은 JSON 데이터 파싱
2. 문제집 HTML 템플릿에 데이터 주입
3. PDF 변환
4. 이메일 전송

> **Template.html** (문제집 템플릿)
> 

실제 굿노트에 저장된 퀴즈 문제집의 모습

<img width="2272" height="1760" alt="image" src="https://github.com/user-attachments/assets/3f395fe9-4954-4f4c-adb6-09cdd6857bfa" />

<img width="2272" height="1760" alt="image" src="https://github.com/user-attachments/assets/fa4ba4fb-d8b6-437e-a1ef-97e658832db9" />


### user input

1. **실시간 생성 시**
    - 굿노트 이메일 (String)
    - 뉴스 기사 링크 (String)
2. **랜덤 생성 시**
    - 굿노트 이메일 (String)

---

## 4. 트러블 슈팅

### JSON vs Array 데이터 타입 불일치

- **문제:** PostgreSQL의 `quiz_data` 컬럼 (JSONB 타입) / 실제 퀴즈 객체 (배열 형태)
    - n8n의 기본 Insert 노드가 단순 문자열 또는 객체로 인식해 `violates check constraint` 에러 발생
- **해결:** n8n의 UI 기반 Insert 대신 Raw SQL Query를 사용
    
    ```sql
    INSERT INTO quizzes (...) VALUES ($1, $2, $3::jsonb)
    ```
    
    - 데이터를 `$3` 파라미터로 넘길 때 `JSON.stringify()`로 감싸서 문자열로 전달 
    → DB가 이를 JSON으로 캐스팅(`::jsonb`)하도록 강제하여 해결

### 비동기 처리와 데이터 정합성 (Fire & Forget 이슈)

- **문제:** Google App Script로 이메일을 보내는 동안 n8n이 기다리지 않고 다음 단계인 DB 삭제를 실행
    - 노드 외부에서 실행되는 GAS의 특성으로 인해 메일 전송은 실패되었으나 데이터가 삭제되거나 메일 발송 전에 데이터가 지워지는 문제 발생
- **해결:**
    1. GAS 요청을 비동기에서(Fire & Forget) → 동기(Wait for response)로 변경
    2. `If` 노드를 추가해 `status: success` 응답을 확인한 후에만 Delete 노드가 실행되도록 트랜잭션 순서(Select → Send → Delete)를 변경

### 결과물 시각화 (Markdown to HTML)

- **문제:** AI가 생성한 줄바꿈 문자(`\n`)가 HTML/PDF에서는 무시되어 텍스트가 뭉개져 보였습니다.
- **해결:** GAS의 `Template.html` 내에 정규식 치환 로직을 추가했습니다.
    - `\n` → `<br>`
    - `# 제목` → `<h3 style="...">제목</h3>` (CSS 스타일링 적용)
    이를 통해 굿노트에서 필기하기 좋은 예쁜 학습지 레이아웃을 완성했습니다.

---

## 5. 회고 및 느낀점

### 💡 Low-code Tool의 한계와 가능성

- n8n은 여러 로직을 시각적인 노드를 통해 간단하게 처리해 생산성을 높일 수 있는 도구
- 하지만 복잡한 DB 로직(JSONB 처리, 트랜잭션 관리)을 처리할 때는 UI만으로 해결되지 않음
- 결국 SQL과 JavaScript 코드에 대한 이해가 뒷받침되어야 노코드 툴을 더 효과적으로 활용할 수 있을 것이라 느낌

### 💡 데이터 구조 설계의 중요성

- 퀴즈 데이터 구조 : `quiz_data`를 통째로 JSON으로 저장
    - [조회 → 발송 → 삭제]로 이어지는 로직을 간단하게 구현
- 서비스의 목적(개별 문제 관리 vs 세트 단위 학습)에 맞는 DB 모델링의 중요성을 체감
