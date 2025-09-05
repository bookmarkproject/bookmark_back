## 🔎 프로젝트 소개

위 프로젝트는 다음과 같은 배경에서 진행되었습니다.

- 책을 읽으면서 인상 깊었던 문장이나 느낀 점들을 기록하고 싶었지만, 노트나 메모장 앱은 체계적으로 관리하기 어렵다는 불편을 느낌.
- 많은 사람들이 책을 읽고도 기록하지 않아 금방 잊어버리는 경우가 많기 때문에 이를 해결하고자 함.
- 독서 경험을 ‘나만의 기록’으로 남길 수 있는 공간이 있으면 좋을 것 같음.

<br>

### 1. 프로젝트 기간

**2025.07.02 ~ 2025.09.04**

<br>

### 2. 포함 내용

1. 프로젝트 소개
2. 사용 기술 스택
3. 서비스 구조,화면,기능
4. 디렉토리 구조 및 패키지 역할
5. API 구조
6. 기술적 이슈 및 해결 과정
7. 관련 논문
8. 프로젝트 팀원 및 역할

<br>

## 🔧 사용 기술 스택

### Backend

- SpringBoot 3.3.2
- Java 17

### Database

- H2 (Local)
- MySQL (Dev , Prod)

### Infra

- AWS EC2
- AWS S3
- Nginx (Https)
- Prometheus & Grafana (monitoring)

### Tools

- GitHub
- Postman

<br>

## 📌 시스템 아키텍처

<img src="readmeAsset/SystemArchitecture.png">

1. AWS EC2를 이용하여 서버 구축
2. Nginx를 이용하여 HTTP -> HTTPS 구현
3. Prometheus + Grafana를 활용하여 Monitoring 시스템 구현

<br>

## 🎁 애플리케이션 아키텍처

<img src="readmeAsset/applicationArchitecture.png">

1. LogFilter를 이용한 로깅 시스템 구축
2. AuthenticationFilter를 이용한 인증 시스템 구현
    - Request Header에서 JWT 토큰을 꺼낸 뒤 유효성 검증
    - Public Endpoint로 지정한 요청 URL은 해당 필터를 거치지 않도록 구현
    - JWT 토큰이 유효하지 않으면 401 응답으로 예외를 발생시킴
3. Controller - Service - Repository 구조로 백엔드 서버 구현
4. Spring Boot Mail (JavaMailSender)을 이용한 이메일 인증번호 발송 시스템 구현
    - Gmail 사용
5. Aladin Open API를 이용한 도서 정보 가져오기 시스템 구현
6. MySQL을 이용한 관계형 데이터 저장

<br> 

## 📂 디렉토리 구조 및 패키지 역할

### Main

```
main
├── java
│   └── com
│       └── example
│           └── bookmarkback
│               ├── auth
│               │   ├── config
│               │   │   └── SecurityConfig.java
│               │   ├── controller
│               │   │   ├── AuthController.java
│               │   │   └── EmailController.java
│               │   ├── dto
│               │   │   ├── AuthCheckType.java
│               │   │   ├── ChangePasswordRequest.java
│               │   │   ├── EmailRequest.java
│               │   │   ├── EmailResponse.java
│               │   │   ├── FindEmailRequest.java
│               │   │   ├── LoginRequest.java
│               │   │   ├── RefreshTokenRequest.java
│               │   │   ├── RefreshTokenResponse.java
│               │   │   └── SignupRequest.java
│               │   ├── entity
│               │   │   ├── EmailVerification.java
│               │   │   └── RefreshToken.java
│               │   ├── infra
│               │   │   ├── JwtUtils.java
│               │   │   ├── LoginJwtUtils.java
│               │   │   └── PasswordChangeJwtUtils.java
│               │   ├── repository
│               │   │   ├── EmailVerificationRepository.java
│               │   │   └── RefreshTokenRepository.java
│               │   └── service
│               │       ├── AuthService.java
│               │       └── EmailService.java
│               ├── book
│               │   ├── cache
│               │   │   ├── BestSellerCache.java
│               │   │   └── LatestBookCache.java
│               │   ├── controller
│               │   │   ├── BookController.java
│               │   │   ├── BookLogController.java
│               │   │   ├── BookLogQuestionController.java
│               │   │   └── BookRecordController.java
│               │   ├── dto
│               │   │   ├── BookLogOverRequest.java
│               │   │   ├── BookLogQuestionResponse.java
│               │   │   ├── BookLogRequest.java
│               │   │   ├── BookLogResponse.java
│               │   │   ├── BookRecordRequest.java
│               │   │   ├── BookRecordResponse.java
│               │   │   └── BookResponse.java
│               │   ├── entity
│               │   │   ├── Book.java
│               │   │   ├── BookLog.java
│               │   │   ├── BookLogQuestion.java
│               │   │   ├── BookRecord.java
│               │   │   ├── LogType.java
│               │   │   └── RecordStatus.java
│               │   ├── repository
│               │   │   ├── BookLogQuestionRepository.java
│               │   │   ├── BookLogRepository.java
│               │   │   ├── BookRecordRepository.java
│               │   │   └── BookRepository.java
│               │   └── service
│               │       ├── AladdinApiService.java
│               │       ├── BookLogQuestionService.java
│               │       ├── BookLogService.java
│               │       ├── BookRecordService.java
│               │       └── BookService.java
│               ├── BookmarkbackApplication.java
│               ├── global
│               │   ├── argumentresolver
│               │   │   └── MemberAuthArgumentResolver.java
│               │   ├── config
│               │   │   ├── RestTemplateConfig.java
│               │   │   ├── S3Config.java
│               │   │   └── WebConfig.java
│               │   ├── dto
│               │   │   ├── ErrorResponse.java
│               │   │   └── MemberAuth.java
│               │   ├── entity
│               │   │   └── BaseEntity.java
│               │   ├── exception
│               │   │   ├── BadRequestException.java
│               │   │   ├── dto
│               │   │   │   └── ExceptionResponse.java
│               │   │   ├── ForbiddenException.java
│               │   │   ├── ResourceNotFoundException.java
│               │   │   ├── RestExceptionHandler.java
│               │   │   └── UnauthorizedException.java
│               │   └── filter
│               │       ├── AuthenticationFilter.java
│               │       ├── LogFilter.java
│               │       └── PublicEndpoint.java
│               ├── member
│               │   ├── controller
│               │   │   └── MemberController.java
│               │   ├── DataInitializer.java
│               │   ├── dto
│               │   │   ├── MemberRequest.java
│               │   │   └── MemberResponse.java
│               │   ├── entity
│               │   │   ├── Gender.java
│               │   │   ├── Member.java
│               │   │   └── Role.java
│               │   ├── repository
│               │   │   └── MemberRepository.java
│               │   └── service
│               │       └── MemberService.java
│               └── test
│                   └── TestController.java
└── resources
    ├── application.yml
    ├── logback-spring.xml
    ├── static
    └── templates
 ```

1. auth : 인증과 관련된 기능을 수행하는 디렉토리
2. book : 도서, 독서 기록과 같은 책과 관련된 기능을 수행하는 디렉토리
3. global : 예외처리, 필터와 같은 전역적인 기능을 수행하는 디렉토리
4. member : 사용자와 관련된 기능을 수행하는 디렉토리
5. test : 간단한 RestAPI 테스트 기능을 수행하는 디렉토리
6. resources : 환경설정, 로깅 설정과 같은 설정 파일의 역할을 수행하는 디렉토리

<br>

### TEST

```
test
├── java
│   └── com
│       └── example
│           └── bookmarkback
│               ├── auth
│               │   ├── controller
│               │   │   └── AuthControllerTest.java
│               │   ├── infra
│               │   │   └── JwtUtilsTest.java
│               │   └── service
│               │       ├── AuthServiceTest.java
│               │       └── EmailServiceTest.java
│               ├── book
│               │   └── service
│               │       ├── BookLogQuestionServiceTest.java
│               │       ├── BookLogServiceTest.java
│               │       ├── BookRecordServiceTest.java
│               │       └── BookServiceTest.java
│               ├── BookmarkbackApplicationTests.java
│               └── global
│                   ├── exception
│                   │   └── RestExceptionHandlerTest.java
│                   └── filter
│                       └── PublicEndpointTest.java
└── resources
    ├── logback-test.xml
    └── test-application.yml
```

main에서 만든 기능을 테스트 하기 위한 패키지 (JUnit)

<br>

## ✅ 기술 적용

1. 커뮤니티 탐지를 위해 어떠한 알고리즘을 사용해야 할까?
    - 초기에 Neo4j GDS 라이브러리의 Louvain 알고리즘을 사용했음.
    - 하지만 커뮤니티를 나눈 결과가 원하는 대로 나오지 않았음.
    - 팀원들끼리 회의한 후 유사한 그룹을 더 잘 표현할 수 있는 Kmeans 알고리즘을 사용하기로 결정함.

2. 여러 패키지에서 DB 연결을 동시에 진행하고 있음.
    - ChatingManager, AuthManager, episodeManager 패키지에서 DB연결을 진행함.
    - 코드의 중복성을 제거하기 위해 전역적으로 설정할 수 있는 globals 패키지에 DB를 연결할 수 있는 util 파일을 만듬.
    - 외부 패키지에서 DBUtil에 있는 connection 변수를 import만 해도 DB를 바로 사용할 수 있다.

<br>

## 🥇 관련 서비스 개발 및 성과

### 1. 프로젝트 실험 결과

<img src="readmeAsset/프로젝트결과.png">
본 프로젝트에서는 다음과 같은 3가지 모델을 두어 비교를 하였다.   

- ChatGPT 와 비교해서는 정확도를 높이고 사용한 토큰 개수는 대폭 줄였다.
- ChatGPT RAG와 비교해서는 정확도를 거의 유지하고 토큰 개수는 2배 정도 줄였다.

본 프로젝트의 결과 정확도를 비슷하게 유지하면서 토큰의 개수를 많이 줄여 경제적인 효율성까지 얻는 결과를 도출하였다.

### 2. 교내 학습 도우미 이루매GPT

<img src="readmeAsset/이루매GPT.png">
본 프로젝트의 지식 그래프, 에피소드를 이용한 RAG 기술을 적용하여 직접 교내 학습 도우미 웹 챗봇 서비스로 구현   

- 새내기 학습 가이드의 내용을 초기에 학습하여 교내 구성원들이 학교 생활에 대해 질문했을 때 도움을 줌.

- 대화를 할 때마다 대화 내용이 저장됨.

- 상단과 하단에 있는 학습 버튼을 통해 개인화가 가능하도록 구현함.

<br>

### 3. 논문 발표

본 프로젝트의 내용을 기반으로 제작한 논문 입니다.   
[논문 보러 가기](readmeAsset/AI캐릭터논문2.pdf)

<br>

### 4. 수상

#### 제5회 인공지능학술대회 장려 논문상

<img src="readmeAsset/논문대회수상.jpg">

#### 서울시립대 공과대학 실전문제연구대회 우수상

<img src="readmeAsset/실전문제수상.jpg">

<br>

## 👬 프로젝트 팀원 및 역할

1. 🐶이세영 [@LSe-Yeong](https://github.com/LSe-Yeong)

- FastAPI를 이용하여 메인 서버 Controller 구현
- JWT를 이용한 사용자 인증 구현
- Neo4j GraphDB를 이용하여 챗봇의 기억 데이터 관리
- MySQL을 이용하여 에피소드 메모리 구현
- 개발 테스트를 위한 AWS EC2 가상머신 배포
- 챗봇 웹 서비스 이루매GPT 프론트엔드 개발

2. 🐰최명재 [@DdingJae418](https://github.com/DdingJae418)
3. 🐱신지호 [@simpack0513](https://github.com/simpack0513)