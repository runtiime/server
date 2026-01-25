# YeChef Backend (Spring Boot)

`server/yechef`는 Ye! Chef 서비스의 백엔드(Spring Boot) 런타임 서버입니다.  
레시피/멤버/좋아요/기록 등 서버 기능과 DB 연동을 담당합니다.

---

## 기술 스택
- Java / Spring Boot
- MySQL 8.0
- Docker / Docker Compose
- GitHub Actions (Docker 이미지 빌드 & EC2 배포)

---

## 디렉토리 구조
server/yechef/
├─ gradle/wrapper/
├─ src/
├─ Dockerfile
├─ docker-compose.yml
├─ build.gradle
├─ gradlew
├─ gradlew.bat
└─ settings.gradle

---

## 실행 방법 (로컬/서버 공통)

### 1) 환경변수(.env) 준비
`docker-compose.yml`에서 `env_file: .env`를 사용합니다.  
아래 항목을 프로젝트 설정에 맞게 채우세요(예시).

- DB
  - `DB_ROOT_PASSWORD`
  - `DB_NAME`
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`

- 외부 연동(사용 중인 경우에만)
  - `OPENAI_MODEL`
  - `OPENAI_API_URL`
  - (Kakao) REST API Key 등

