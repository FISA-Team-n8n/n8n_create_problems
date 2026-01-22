# 🤖 뉴즈 (Newz) - (News + Quiz) 뉴스를 풀다!
n8n을 사용한 경제/금융 뉴스로부터 문제 만들기 자동화 프로젝트

<br/>

## 👩🏻‍💻 팀원 소개

| <img src="https://avatars.githubusercontent.com/u/178015712?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/118096607?v=4" width="150" height="150"/> | <img src="https://avatars.githubusercontent.com/u/89902255?v=4" width="150" height="150"/> | 
| :-----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------: |  
|                 권순재<br/>[@Soooonnn](https://github.com/Soooonnn)                 |                       서가영<br/>[@caminobelllo](https://github.com/caminobelllo)                       |                유예원<br/>[@Yewon0106](https://github.com/Yewon0106)                |   


<br />


## 💡 기획 배경

### Pain Point

> 매일 수많은 경제 뉴스가 쏟아지지만
이를 매일 읽는 것도 어렵고, 읽더라도 지식을 내 것으로 만들기는 어려움
> 

→ **AI가 금융 기사를 분석해 굿노트용 학습지로 생성하여 제공하여 사용자의 능동적 학습 유도**

<br>

### 핵심 목표

1. **능동적 학습:**  요약 뿐 만 아니라 객관식/주관식 퀴즈를 통한 사고력을 확장
2. **자동화:** 랜덤으로 문제를 추출 또는 URL을 넣으면 문제 생성 → PDF 변환 → 굿노트 어플로 저장 자동화

<br/><hr/>

## 🛠️ Tech Stack
- **Orchestration:** n8n (워크플로우 자동화)
- **Database:** PostgreSQL (데이터 영구 저장 및 관리)
- **AI Engine:** OpenAI GPT-4.1-NANO (뉴스 요약 및 퀴즈 생성)
- **Frontend/Trigger:** HTML Dashboard (사용자 입력 및 제어)
- **PDF/Email:** Google Apps Script (HTML 템플릿 렌더링 및 메일 발송)
- **Notification:** Discord (알림 발송)

  <br/>
  
<img src="https://img.shields.io/badge/n8n-EA4B71?style=for-the-badge&logo=n8n&logoColor=white"> ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![Supabase](https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white) ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![Discord](https://img.shields.io/badge/Discord-%235865F2.svg?style=for-the-badge&logo=discord&logoColor=white) 

<br/><hr/>

## 📊 데이터베이스 (PostgreSQL, Supabase)
> **PostgreSQL 기반의 Supabase :** 금융 기사 데이터, AI 요약 결과, 문제 생성 결과를 구조적으로 저장 및 관리할 수 있는 데이터베이스
> 

<br/>

### ⚙️ ERD

> 기사 수집 → 요약 → 문제 생성 → PDF 출력으로 이어지는 전체 자동화 흐름을 안정적으로 관리할 수 있도록 데이터 구조 설계

<img width="1402" height="899" alt="image" src="https://github.com/user-attachments/assets/d686bf90-4da5-4ce0-814c-0c125cc7abc2" />

<br/>

### 📑 스키마 구조

> 관계형 DB를 사용하되 유연성을 위해 **JSONB** 타입을 활용
> 
- **articles**: 크롤링된 금융 기사 원문과 AI 요약 데이터 저장
- **quizzes**: 기사별로 생성된 금융 시사 문제 세트 저장
    - quiz_data 컬럼을 **JSONB**로 설계해 [객관식, 주관식, 정답, 해설]이 담긴 **배열(Array)** 전체를 하나의 행에 저장
- **execution_logs**: 실행 이력 및 상태 추적

<br/>

### 🐘 PostgreSQL 선택 이유

- 관계형 데이터베이스로서 [ 기사 → 실행 로그 → 퀴즈 ] 간의 명확한 관계(FK)를 설계하기에 적합
- JSONB 타입을 통해 AI가 생성한 문제 데이터 저장 가능
- 트랜잭션, 인덱스, 제약조건 등 데이터 무결성과 확장성 확보 가능

|  | MySQL | PostgreSQL |
| --- | --- | --- |
| **구분** | RDBMS | ORDBMS |
| **데이터 타입 확장성** | JSON 지원은 있으나 기능 제한적 | JSONB, ENUM, ARRAY 등 고급 타입 지원 |
| **JSON 데이터 처리** | JSON 내부 필드 직접 쿼리,인덱싱 가능 | 상대적으로 제약 조건 활용 제한 |
| **Supabase 호환성** | Supabase 기본 DB | Supabase 미지원 |

<br/>

### ⚡ Supabase 선택 이유

- PostgreSQL을 기반으로 한 관리형 DB 서비스로 빠른 프로젝트 세팅 가능
- SQL Editor를 통한 직접적인 스키마 관리 및 디버깅 용이
- n8n과의 연동 시 Postgres Pooler 연결을 지원하여 안정적인 자동화 환경 구축 가능

<br/>

### 🔗 데이터베이스 ↔ n8n 연동 방식

- Docker 기반 n8n 환경에서 Postgres Node를 사용하여 Supabase DB에 직접 접근
- SELECT / INSERT / JOIN 쿼리 활용해
    - 자동화 결과 검증
    - 실패 로그 추적
    - AI 생성 데이터 확인
 
<br/><hr/>

## 🔑 핵심 기능 구현

### 1️⃣ n8n

> 전체 워크플로우

<img width="2138" height="1006" alt="image" src="https://github.com/user-attachments/assets/e66b7a2b-8a05-4be4-9b82-5115b2039363" />

<br/>

### A. 실시간 생성 모드 (URL Trigger)
<img width="2186" height="388" alt="image" src="https://github.com/user-attachments/assets/7deb61b5-9e05-4bb1-9eda-452951e88ec9" />

1. 사용자가 대시보드에서 뉴스 URL & 굿노트로 발송받을 이메일 주소를 입력
2. n8n이 기사 본문을 크롤링
3. OpenAI가 Markdown 형식으로 요약 및 퀴즈(객관식/주관식) 생성
4. 데이터를 데이터베이스에 적재
5. Google Apps Script(GAS)로 데이터 전송 → HTML 템플릿 렌더링 → PDF 변환 → 이메일 발송

<br/>

### B. 랜덤 문제 생성 모드 (Random Trigger)
<img width="2554" height="330" alt="image" src="https://github.com/user-attachments/assets/d02bf191-4f78-438c-9cee-7737cfb1cd2f" />

1. DB(`quizzes` + `articles` 조인)에서 `ORDER BY RANDOM() LIMIT 1` 쿼리로 과거 문제 1세트 추출
2. 저장된 JSON 데이터를 다시 Markdown 텍스트로 역직렬화
3. GAS로 전송하여 PDF 발송
4. 발송 완료된 문제는 데이터베이스에서 자동 삭제하여 중복 학습 방지

<br/>

### C. 스케줄링을 통한 데이터 크롤링 
<img width="2070" height="384" alt="image" src="https://github.com/user-attachments/assets/84712cf0-f5fc-4a55-8a15-0bbe1b485d80" />

1. 하루 단위로 한국 경제 사이트의 금융 카테고리 기사를 크롤링 및 개별 뉴스 URL 추출
2. 기사 본문을 JSON 형태로 데이터베이스에 적재
3. 각 기사의 본문을 파싱
4. OpenAI가 Markdown 형식으로 요약 및 퀴즈(객관식/주관식) 생성
5. 퀴즈 데이터를 데이터베이스에 적재
6. 임계치의 퀴즈가 생성되면 사용자에게 디스코드로 알림 전송

<br/>

**문제 output 포맷**
```json
[
  {
    "output": [
      {
        "id": "uuid",
        "type": "",
        "status": "",
        "content": [
          {
            "type": "",
            "annotations": [],
            "logprobs": [],
            "text": "{}"
        ],
        "role": "assistant"
      }
    ]
  }, 
  ...
]
```

<br/>

### 2️⃣ 사용자 대시보드
```bash
로컬 Python 서버 실행 명령어
>> python3 -m http.server 8000
```

<img width="3248" height="1956" alt="image" src="https://github.com/user-attachments/assets/1bdc28e3-969c-4c81-a139-b9558fa13216" />

**✨ User Input**

1. **실시간 생성 시**
    - 굿노트 이메일 (String)
    - 뉴스 기사 링크 (String)
2. **랜덤 생성 시**
    - 굿노트 이메일 (String)

1. n8n Webhook URL을 연결
2. 사용자가 버튼 트리거 
3. 두 가지 모드 (url or 랜덤) 방식으로 분기된 상태 n8n 노드에 전달

<br/>

### 3️⃣ Google App Script

> **Code.gs**  (로직 처리)
> 
1. n8n으로부터 전달받은 JSON 데이터 파싱
2. 문제집 HTML 템플릿에 데이터 주입
3. PDF 변환
4. 이메일 전송

<br/>

> **Template.html** (문제집 템플릿)
> 

**실제 굿노트에 저장된 퀴즈 문제집의 모습**
<img width="2272" height="1760" alt="image" src="https://github.com/user-attachments/assets/f579a2bc-f054-4fa0-b1c0-f19e73433c03" />
<img width="2272" height="1760" alt="image" src="https://github.com/user-attachments/assets/10a8fa4f-8e93-4e50-aac4-dba2c8f536ac" />

<br/>

### 4️⃣ Discord 알림
<img width="1344" height="906" alt="image" src="https://github.com/user-attachments/assets/08c3ea09-12a9-4bd8-bc33-e1d5d3384176" />

- DB에 있는 퀴즈 데이터가 IF노드에서 설정한 임계값을 초과
  <br/>
    → 위 사진과 같은 형식으로 **사용자 알림 전송**

<br/><hr/>

## 🎯 트러블 슈팅 
<img width="629" height="443" alt="image" src="https://github.com/user-attachments/assets/ca8e47c3-0b19-4420-b49c-d9637dd686f5" />

<br>

### 1. **LLM 응답의 비정형성으로 인한 데이터 구조화의 어려움**

- **문제:** LLM이 생성한 퀴즈 데이터가 일정한 JSON 형식이 아니거나, 답변에 불필요한 텍스트가 포함되어 후속 노드에서 데이터를 식별하는 데 어려움이 발생
- **해결**: 시스템 프롬프트에 출력 형식을 JSON으로 제한하고, 예시 데이터를 제공하여 출력의 일관성을 향상시켜 해결

<br/>

### 2. JSON vs Array 데이터 타입 불일치

- **문제:** PostgreSQL의 quiz_data 컬럼 (JSONB 타입) / 실제 퀴즈 객체 (배열 형태)
    - n8n의 기본 Insert 노드가 단순 문자열 또는 객체로 인식해 `violates check constraint` 에러 발생
- **해결:** n8n의 UI 기반 Insert 대신 Raw SQL Query를 사용
    
    ```sql
    INSERT INTO quizzes (...) VALUES ($1, $2, $3::jsonb)
    ```
    
    - 데이터를 `$3` 파라미터로 넘길 때 `JSON.stringify()`로 감싸서 문자열로 전달 
    → DB가 이를 JSON으로 캐스팅하도록 강제하여 해결

<br/>

### 3. 비동기 처리와 데이터 정합성

- **문제:** Google App Script로 이메일을 보내는 동안 n8n이 기다리지 않고 다음 단계인 DB 행 삭제를 실행
    - 노드 외부에서 실행되는 GAS의 특성으로 인해 메일 전송은 실패되었으나 데이터가 삭제되거나 메일 발송 전에 데이터가 지워지는 문제 발생
- **해결:**
    1. GAS 요청을 비동기에서→ 동기 방식으로 변경
    2. If 노드를 추가해 `status: success` 응답을 확인한 후에만 Delete 노드가 실행되도록 트랜잭션 순서(Select → Send → Delete)를 변경

<br/>

### 4. 결과물 시각화 (Markdown to HTML)

- **문제:** AI가 생성한 줄바꿈 문자(`\n`)가 HTML과PDF에서는 무시되어 텍스트가 뭉개짐
- **해결:** GAS의 Template.html 내에 정규식 치환 로직을 추가
    - `\n` → `<br>`
    - `# 제목` → `<h3 style="...">제목</h3>` (CSS 스타일링 적용)
    - 이를 통해 굿노트에서 필기하기 좋은 UI 생성

<br/>

### **5. 다량의 데이터 처리 시 워크플로우 중단**

- **문제:** 뉴스 목록에서 한 번에 다량의 기사를 가져올 때 LLM 노드에서 전체 프로세스가 중단되는 문제가 발생
- **해결:** Limit 노드를 배치하여 AI가 처리해야 하는 뉴스 데이터의 양 감소

<br/><hr/>

## 💁 회고 및 느낀점

### 1. Low-code Tool의 한계와 가능성

- n8n은 여러 로직을 시각적인 노드를 통해 간단하게 처리해 생산성을 높일 수 있는 도구
- 하지만 복잡한 DB 로직(JSONB 처리, 트랜잭션 관리)을 처리할 때는 UI만으로 해결되지 않음
- 결국 SQL과 JavaScript 코드에 대한 이해가 뒷받침되어야 노코드 툴을 더 효과적으로 활용할 수 있을 것이라 느낌

### 2. 데이터 구조 설계의 중요성

- 퀴즈 데이터 구조 : `quiz_data`를 통째로 JSON으로 저장
    - [조회 → 발송 → 삭제]로 이어지는 로직을 간단하게 구현
- 서비스의 목적(개별 문제 관리 vs 세트 단위 학습)에 맞는 DB 모델링의 중요성을 체감

<br/>
<hr/>

## 🚀 향후 발전 가능성

> 본 프로젝트는 금융 기사 기반 AI 학습 콘텐츠 자동화를 출발점으로 하여, 향후 안정적인 파이프라인 운영, 문제 품질 관리, 개인화 학습 기능 확장을 통해 금융 학습을 지원하는 AI 기반 플랫폼으로 발전시킬 수 있음
> 

- **사용자 개념 도입**
    - 학습 이력
    - 틀린 문제 관리
    - 선호 난이도 관리

👉 **개인화 학습 서비스로 확장 가능**

- **대시 보드화**
    - 일별 기사 수
    - 실패율
    - 주요 키워드 트렌드

👉 **보여지는 결과물 → 학습 동기부여**
