# Springboot-Security-JWT-V3

## JWT 실습 전 알아야 할 배경지식

### Session 이란?
(이해를 위해 쉬운 표현으로 설명함)
- 웹 브라우저에서 서버로 요청을 보내면 서버는 응답 헤더에 `Session ID` 를 반환
- 웹 브라우저에서는 응답받은 세션 정보를 브라우저 내에 저장
- 이후, 웹 브라우저에서 서버로 요청을 보낼 때, 최초 응답받은 `Session ID`가 포함됨
- `Session ID` 가 삭제되는 경우
    - 서버 측에서 Session 정보를 삭제 시
    - 클라이언트 측에서 브라우저 종료 시
    - 일정 시간(30분) 경과 후

### Session 로그인 프로세스
- 웹 브라우저에서 클라이언트가 서버로 최초 요청
- 서버는 `Session` 에 `Session ID` 생성
- `Session ID` 가 가지고 있는 저장소 생성
- 서버에서 클라이언트로 응답 시 헤더에 `Session ID` 반환
- 클라이언트 측 웹 브라우저에 `Session ID` 저장
- 클라이언트 측에서 서버로 로그인 요청
- 서버에서 로그인 요청 데이터에 대한 DB 조회
- 정상 로그인 데이터로 확인 시 `Session ID` 가 가지고 있는 저장소에 회원 (User) 정보를 저장
- 정상 로그인 시 메인 페이지로 반환
- 클라이언트 측에서 서버로 인증이 필요한 페이지 요청 시 `Session ID` 가 가지고 있는 저장소에 회원 (User) 정보가 있는지 확인

### Session 의 단점
- 동시접속자 수가 일정 이상 증가 시, 서버에 부하가 발생하지 않도록 분산 처리 (로드 밸런싱)
- 클라이언트 측에서 최초 요청한 서버가 아닌 다른 서버에 다음 요청을 보내게 될 경우, Session 정보가 없은 상황이 발생
- 각 서버에서 Session 정보를 공유할 메모리 공유 서버가 필요
    - DB에 저장할 경우, 속도 저하 등 성능 이슈 발생
    - 메모리에 경우 CPU 가 아닌 RAM 을 통해 전기적 신호로 조회하기 때문에 IO 가 발생하는 DB 조회보다 메모리 조회가 성능 측면에서 월등함
    - 대표적인 메모리 서버 : REDIS

### TCP
- OSI 7계층
    1. 물리
    2. 데이터 링크
    3. 네트워크
    4. 전송
    5. 세션
    6. 표현
    7. 응용
- TCP 통신은 신뢰성 있는 통신
    - A -> B 로 데이터 전송 시 B 는 응답으로 Ack 반환
- UDP 통신은 비신뢰성 통신
    - ex) 전화

### 보안의 3요소 CIA (Confidentiality, Integrity, Availability)
- 기밀성 (Confidentiality)
    - 소유자가 원하는대로 정보의 비밀을 유지한다.
    - 명백히 허가된 대상에게만 정보가 제공되어야 한다.
    - 접근통제와 암호화로 구현한다.
- 무결성 (Integrity)
    - 비인가자에 의한 정보의 변경, 삭제, 생성을 막는다.
    - 정보의 정확성과 안정성을 보장한다.
    - 물리적 통제와 접근 통제로 무결성을 보장한다.
- 가용성 (Availability)
    - 적절한 방법으로 작동되고 정당한 방법으로 필요한 시점에 권한이 부여된 사용자에게 정보가 제공된다.
    - 백업, 중복성 유지, 위협 요소로부터의 보호 등으로 가용성을 보장한다.

### RSA 암호 알고리즘
- RSA 란?
    - Rivet, Shamir, Adelman 세사람의 첫이름을 따 RSA라고 만든 암호 알고리즘
    - RSA 암호 체계는 미국 MIT에서 개발한 공개키 암호 시스템이다.
    - 이 암호 알고리즘의 핵심은 큰 정수의 소인수 분해가 어렵다는 점을 이용하여 암호화를 시킨다.
    - 이러한 RSA 암호 알고리즘은 전자상거래에서 가장 흔히 쓰고있는 공개키 알고리즘이다.
- RSA 방식
    - A가 B에게 정보를 안전하게 보내고 싶어한다. 이때 RSA 알고리즘을 이용하고자 한다.
    - B가 공개키와 개인키를 만들어 A에게 공개키를 보낸다. (개인키는 B만 가지고 있다.)
    - A가 B로부터 받은 공개키를 이용하여 보낼 정보를 암호화한다.
    - A가 암호화된 정보를 B에게 보낸다.
    - B가 암호화된 정보를 받고 개인키를 이용하여 암호를 해독한다.

<hr>

## JWT 구조 이해

### JWT 란?
- 관련 공식 문서
    - [RFC 7519 문서 링크](https://www.rfc-editor.org/rfc/rfc7519)
    - [JWT 공식 문서 링크](https://jwt.io/introduction)
- JWT (JSON Web Token) 는 당사자간에 정보를 JSON 객체로 안전하게 전송하기 위한 컴팩트하고 독립적인 방식을 정의하는 개방형 웹 표준 (RFC 7519)
- 이 정보는 디지털 서명이 되어있으므로 확인하고 신뢰할 수 있음
- JWT 는 비밀 (HMAC 알고리즘 사용), RSA, ECDSA 를 사용하는 공개 / 개인 키 쌍을 사용하여 서명할 수 있음

### JWT 로그인 프로세스
- Spring Security 로그인 프로세스
  - 클라이언트로 부터 요청 받은 `username`, `password` 로 인증 진행
  - 정상적으로 인증되면 서버에서 `Session ID` 생성
  - 생성된 `Session ID` 를 `Cookie` 에 담아서 응답
  - 이후, 클라이언트에서 요청할 때마다 응답 받은 `Session ID` 를 가지고 서버에 요청
  - 서버는 `Session ID` 유효 여부 확인 후, 인증이 필요한 페이지 접근 허용
- JWT 로그인 프로세스
  - 클라이언트로 부터 요청 받은 `username`, `password` 로 인증 진행
  - 정상적으로 인증되면 `JWT 토큰` 생성
  - 클라이언트로 생성한 `JWT 토큰` 응답
  - 이후, 클라이언트에서 요청할 때마다 응답받은 `JWT 토큰` 을 가지고 서버에 요청
  - 서버는 필터를 통해 `JWT 토큰` 의 유효 여부 확인 후, 인증이 필요한 페이지 접근 허용 