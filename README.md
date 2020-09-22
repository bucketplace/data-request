# data-request
데이터 SQL/추출/분석 요청 도구

### 개발&실행 환경
---
- 서버 인프라 : Google Cloud Platform Compute Engine
- 서버 언어 : kotlin
- 서버 프레임워크 : Ktor
- 클라이언트 : Slack Command
- API 서비스 : Jira API, Slack API

### 프로젝트 구조
- src/db : JSON Db와 매핑할 클래스들이 있음
- src/di : 경량 DI 프레임워크 Koin 설정 파일(.kt)이 있음
- src/enums : enum 클래스들의 모음. 사내 멤버 닉네임이 노출되기 때문에 패키지 자체를 git ignore 처리함
- src/routes 
    - commands : 슬랙 명령어 처리하는 클래스들의 모음
    - interactions : 슬랙 팝업에서 확인 [등록] 버튼을 눌렀을 때 처리하는 클래스들의 모음
    - members : 주기적으로 슬랙 멤버 전체 목록을 내장DB에 갱신시켜주는 클래스들의 모음
- src/secrets : 계정 및 패스워드와 같은 시크릿 데이터 클래스들의 모음. 패키지 자체를 git ignore 처리함
- src/utils : HTTP, API 처리 관련 유틸 클래스들의 모음

