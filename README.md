#-1일차-

---
### 🔷 1️⃣초기화 기본 순서

1. **AutoConfiguration**  
2. **SecurityBuilder**  
3. **SecurityConfiguration**  
4. **HttpSecurityConfiguration**  
5. `HttpSecurity` 생성 및 빌드 (`HttpSecurity.build()` in `HttpSecurityConfiguration.java`)  
6. **SecurityFilterChain** 생성  
7. **WebSecurityConfiguration**  
8. **WebSecurity**  
9. **FilterChainProxy** 생성  

---
### 🔷 2️⃣ 단계별 설명
1. 자동 설정에 의해서 SecurityBuilder 생성
2.interface웹보안을 구성하는 빈객체와 설정 클래스를 생성하는 역활 대표적으론 HttpSecurity, WebSecurity가 있다. SecurityConfiguer를 참조한다. 
3. interface 설정클래스 생성 및 초기화 작업진행(init(B uilder), Configure(B builder) -> SrpingBootWebSecurityConfiguration.java SecurityFilterChain에서 .build
4. HttpSecurity 를생성하고 초기화 하여 최종적으로 SecurityFilterChain 빈 생성 
5. @Scope(Prototype: 매번 요청 마다 새로운 인스턴스) SecurityFilterChain 만들기 위한 빌더
6. getFilter: SecurityFilterChain에 포함된 객체 리스트 반환,각 필터는 특정작업 요청 처리 , boolean mathcer SecurityFilterChain에 의해 처리되어야 하는지 여부 결정 httpSecurity 가 빌더 
7. WebSecurity를 생성하고 초기화
8. HttpSecurity에서 생성한 SecurityFilterChai빈을  SecurityBuilder에 저장, build() 실행하면 SecurityBuilder에서 SecurityFilterChaindmf 꺼내 FilterChainProxy생성자에 전달
9. SecurityFilterChains(List)에 모든 SecurityFilterChain 저장

---
### 🔷 Filter란?

- **서블릿 Filter**는 웹 애플리케이션에서 클라이언트 요청과 서버 응답을 가공·검사하는 역할을 합니다.  
- **WAS(서블릿 컨테이너)**에서 실행 및 종료됩니다.  
- 기본 생명주기 메서드:
  - `init()`: 초기화  
  - `doFilter()`: 요청 전/후 처리 및 다음 필터로 전달  
  - `destroy()`: 필터 제거  

---

## 🔷 DelegatingFilterProxy란?

- **서블릿 필터 역할 + 스프링의 의존성 주입, AOP 기능과 연동**되도록 설계된 스프링 필터입니다.  
- 서블릿 컨테이너와 스프링 ApplicationContext를 연결해주는 **중간 다리 역할**을 합니다.

### 🔎 동작 방식

1️⃣ DelegatingFilterProxy는 톰캣(서블릿 컨테이너)에 Filter로 등록됩니다.  
2️⃣ 요청마다 `doFilter()`가 호출되면, 내부적으로 ApplicationContext에서 `"springSecurityFilterChain"` 이름의 빈을 찾아서 대신 실행합니다.  
3️⃣ `springSecurityFilterChain` 빈은 **FilterChainProxy**이며,  
4️⃣ FilterChainProxy가 URL을 기준으로 적절한 **SecurityFilterChain**을 선택해 순서대로 필터를 실행합니다.

✅ 따라서, **DelegatingFilterProxy는 톰캣 Filter로 요청을 받고, 실제로는 스프링의 FilterChainProxy에게 요청을 위임하는 “브릿지” 역할**을 합니다.
   
 ✅ 스프링 시큐리티와 DelegatingFilterProxy의 연결

🔷 동작 순서

1️⃣ 톰캣이 요청을 받으면 → 등록된 FilterChain 순서대로 doFilter() 호출


2️⃣ DelegatingFilterProxy의 doFilter()가 실행됨


3️⃣ 내부적으로 ApplicationContext에서 “springSecurityFilterChain” 이름의 빈을 찾아서


4️⃣ 그 빈의 doFilter()를 대신 실행(FilterChainProxy)

#-2일차

---
### 인증 프로세스 
#### UsernamePasswordAuthenticationFilter : AbstractAuthenticationProcessingFilter -> AttemptAuthentication() -> UsernamePasswordAuthenticationFilter, CustomAuthenticationFilter
1.AbstractAuthenticationProcessingFilter 확장한 클래스로 HttpServletRequest에서 제출된 사용자 이름과 비밀번호로 부터 인증을 수행한다. 
2. 인증 프로스세그 초기회 될떄 로그인 페이지와 로그아웃 페이지 생성을 위한 Default login,logoutFilter가 초기화 된다
3. RequestMatcher가 클라인트 요청정보가 매칭 되는지 확인 후 false : chaind.doFilter(다음필터로) true: UsernamePasswordAuthenticationToken -> AuthenticationManager(Id,Password) DB랑 비교

### AuthenticationManager 인증 성공시
1.UsernamePAsswordAuthenticationToken `UserDetails`와 `Authorities`를 채움.
2.SeesionAuthenticationStrategy 새로운 로그인을 알리고 세션 관련 작업 수행
3.SecurityContextHolder Authentication 을 SecurityContext에 설정 세션에 SecurityContex저장(인증 상태 유지)
4.RememberMeServies  Id,Password 기억하기 기능 

•	RememberMeAuthenticationFilter: 요청이 들어올 때 쿠키가 있으면 인증 복원 시도

•	RememberMeServices: 실제 쿠키 생성/검증 로직 수행


5.ApplicationEventPyublisher 인증 성공 이벤트 게시
6.AuthenticationSuccessHandler 인증 성공 핸들러 호출  (리다이렉트,메시지 등)

### AuthenticationManager 인증 실패시
1.SecurityContextHolder 삭제
2.RemeberMeServices (RememberMeServices.logiinFail 호출 -> Remeber-me 쿠키/정보 초기화
3.AuthenticationFailureHanlder 호출 (실패 페이지 이동)

   
