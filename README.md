## 목차
- [초기화 기본 순서](#초기화-기본-순서)
- [단계별 설명](#단계별-설명)
- [Filter란](#filter란)
- [DelegatingFilterProxy란](#delegatingfilterproxy란)
- [인증 상태 RememberMeAuthenticationFilter](#RememberMeAuthenticationFilter)
- [로그아웃 처리](#LogOut)
- [요청 캐시 RequestCache SavedRequest](#요청-캐시)
- [인증 아키텍처](#인증-아키텍처)



---
### 초기화 기본 순서

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
### 단계별 설명
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
###  Filter란?

- **서블릿 Filter**는 웹 애플리케이션에서 클라이언트 요청과 서버 응답을 가공·검사하는 역할을 합니다.  
- **WAS(서블릿 컨테이너)**에서 실행 및 종료됩니다.  
- 기본 생명주기 메서드:
  - `init()`: 초기화  
  - `doFilter()`: 요청 전/후 처리 및 다음 필터로 전달  
  - `destroy()`: 필터 제거  

---

##  DelegatingFilterProxy란?

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


---
### 인증 프로세스 
#### UsernamePasswordAuthenticationFilter : AbstractAuthenticationProcessingFilter -> AttemptAuthentication() -> UsernamePasswordAuthenticationFilter, CustomAuthenticationFilter
1.AbstractAuthenticationProcessingFilter 확장한 클래스로 HttpServletRequest에서 제출된 사용자 이름과 비밀번호로 부터 인증을 수행한다. 

2. 인증 프로스세스 초기화 될떄 로그인 페이지와 로그아웃 페이지 생성을 위한 Default login,logoutFilter가 초기화 된다

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



### 인증 실패
- `SecurityContextHolder`의 인증 정보 제거  
- `RememberMeServices.loginFail()`로 Remember-Me 정보 초기화  
- `AuthenticationFailureHandler`로 실패 페이지 이동 처리  

---
## RememberMeAuthenticationFilter
: 인증 상태 SecurityContextHolder에 Authertication이 포함되지 않은 경우 실행되는 필터
1.SecurityContextHolder


1️⃣ 인증(Authentication) 저장소
	•	로그인 성공 시, Authentication 객체를 생성하여 SecurityContext에 넣습니다.
	•	예: UsernamePasswordAuthenticationToken 등의 객체.

2️⃣ 스레드 안전성 보장
	•	기본적으로 ThreadLocal 전략을 사용하여 요청 처리 스레드별로 보안 정보를 안전하게 유지합니다.

 #### 프로세스
 ##### Authentication == null :null 아니면 chain.doFilter
 RememberMeAuthenticationFilter  -> RememberMeServices.autologin() -> RememberMeAuthenticationToken(userDetails + Authorities) -> AuthenticationManager
 -> RmemberMeAuthenticationToken -> SecurituyContext -> SecurityContextRepository -> ApplicationEventPublisher

 ---
 ## LogOut
 DefaultLogoutPageGenerationFilter를 통해 로그아웃 페이지를 Get/logout URL을 기본적으로 제공 

 
 1. 로그아웃 실행은 기본적으로 POST/logout dmfhaks rksmdgksk CSRF 기능을 비성활 할 경우 혹은 RequestMatcher 를 사용할 경우 GET,PUT,DELETE 모두 가능

 2. 
 3. 기본적으로 logOutFilter를 제공하지만 스프링MVC 에서 커스텀 하게 생성 가능함, **로그인 페이지가 커스텀 하게 생성될 경우 로그아웃 기능도 커스텀하게 구현해야 한다**

### 프로세스
LogOutFilter -> RequestMatcher -> LogoutHandler -> LogoutSuccessHandler

---

## 요청 캐시
### RequestCache, SavedRequest

1.RequestCache(Interface): 인증 절차 문제로 리다이렉트 된 후에 이전 요청을 담고 있는 SavedRequest객체를 쿠키 혹은 세션에 저장 하고 다시 필요시 가져와 실행 하는 캐시 메카니즘 HttpSessionRequestCache (구현체)


2.SavedRequest(Interface) : 로그인과 같은 인증 절차 후 사용자를 인증 이전의 원래페이지로 안내하여 이전 요청과 관련된 여러 정보 저장 DefaultSavedREquest(구현체)

### RquestCacheAwareFilter : 이전에 저장했던 웹 요청을 다시 불러오는 역활, SavedRequest가 현재 Request와 일치하면 이 용청을 필터 체인의 doFilter로 저장, 없으면 원래 Request 진행

### 프로세스
1️⃣ 인증이 필요한 URL로 사용자가 접근
2️⃣ ExceptionTranslationFilter가 요청을 가로채서 “인증 필요”를 확인
3️⃣ RequestCache.saveRequest()가 실행되며 현재 요청 데이터를 SavedRequest로 생성
4️⃣ 생성된 SavedRequest(DefaultSavedRequest)를 세션에 저장

➡️ 로그인 성공 후에는 RequestCache.getRequest()로 SavedRequest를 꺼내서,
사용자를 원래 요청 URL로 리다이렉트할지 결정!
RquestCacheAwareFilter -> SavedRequest (null -> chain.doFilter) --> SavedRequest == currentRequest -> chain.doFilter(savedRequest,reponse)


---

### 인증 아키텍처

1. **인증 (Authentication)**  
2. **보안 컨텍스트 (SecurityContext & SecurityContextHolder)**  
3. **인증 관리자 (AuthenticationManager)**  
4. **인증 제공자 (AuthenticationProvider)**  
5. **사용자 상세 서비스 (UserDetailsService)**  
6. **사용자 상세 정보 (UserDetails)**  

---

### 1. 인증 (Authentication)  
시큐리티의 인증 및 인가 흐름은 다음과 같이 진행됩니다:  
**ServletFilter** → **Authentication** → **Authorization** → **Spring MVC**

---

### 흐름도

- **ServletFilter**:  
  `DelegatingFilterProxy` → `FilterChainProxy` (SecurityFilterChain)

- **Authentication**:  
  `AuthenticationFilter` → `Authentication` ←→ `AuthenticationManager` →  
  `AuthenticationProvider` (`PasswordEncoder`) ←→ `UserDetailsService` ←→ `UserDetails` →  
  `SecurityContextHolder` (`SecurityContext`, `Authentication`, `UserDetails`)

- **Authorization**:  
  `AuthorizationFilter` ←→ `AuthorizationManager` ←→ `AuthorizationDecision`

### 용어 및 과정

1. **Authentication (인증)**  
   인증은 특정 자원에 접근하려는 사람의 신원을 확인하는 절차를 의미합니다. 사용자의 인증 정보를 저장하는 객체인 `Authentication`을 통해 인증 상태를 관리합니다. 인증이 완료되면 `SecurityContext`에 저장되어 전역적으로 참조됩니다.

2. **Principal (자바의 Principal 상속)**  
   자바의 `Principal` 인터페이스를 상속하여 사용자의 고유 식별자(주로 사용자 이름)를 나타냅니다.

3. **Authentication 객체의 주요 속성**  
   - `principal` (주로 사용자 이름)  
   - `credentials` (주로 비밀번호)  
   - `authorities` (권한 정보)  
   - `authenticated` (인증 상태)  
   이들은 보안상의 이유로 `Authentication` 토큰 객체로 캡슐화되어 전달됩니다.

4. **AuthenticationManager 처리 이후**  
   - `principal`은 인증된 `UserDetails` 객체로 대체됩니다.  
   - `credentials`는 이미 인증되었기 때문에 `null`로 설정됩니다.  
   - `authorities`는 `ROLE_USER` 같은 `GrantedAuthority` 목록으로 채워집니다.  
   - `authenticated`는 `true`로 설정됩니다.  

5. **SecurityContextHolder**  
   최종적으로 `AuthenticationFilter`는 `SecurityContextHolder`를 통해 `SecurityContext`에 `Authentication`과 `UserDetails`를 저장합니다.

---
## 인증 컨텍스트

### SecurityContext
- 현재 인증된 사용자의 Authentication 객체를 저장
- SecurityContextHolder를 통해 ThreadLocal 저장소에 보관되어, 각 스레드가 독립적으로 보안 컨텍스트를 유지
- 애플리케이션 전반에서 접근 가능 (현재 사용자 인증 상태·권한 확인)

- 참조: SecurityContextHolder.getContext()
- 삭제: SecurityContextHolder.clearContext()

---

### SecurityContextHolder
- SecurityContext를 저장하는 클래스
- SecurityContextHolderStrategy(전략 패턴)를 통해 다양한 저장 전략을 지원
- 기본 전략: MODE_THREADLOCAL (서버 환경에서 안전)
- 전략 모드 직접 지정: SecurityContextHolder.setStrategyName(String)
  - MODE_THREADLOCAL: 각 스레드가 독립적인 보안 컨텍스트 유지
  - MODE_INHERITABLETHREADLOCAL: 부모 스레드의 컨텍스트를 자식 스레드가 상속
  - MODE_GLOBAL: 모든 스레드가 단일 보안 컨텍스트를 공유 (테스트/간단한 앱에만 적합)

**쓰레드 재사용으로 인한 보안 문제를 방지하기 위해, 응답 직전에 항상 SecurityContext를 삭제해야 함.**

---

### AuthenticationManager
- 인증 필터 등으로부터 Authentication 객체를 전달받아 인증 시도
- 여러 AuthenticationProvider를 관리하며 적절한 Provider를 찾아 인증 처리
- AuthenticationManagerBuilder로 생성 (주로 ProviderManager가 구현체)

#### AuthenticationManagerBuilder
- AuthenticationManager를 구성하는 빌더
- UserDetailsService, AuthenticationProvider 등을 설정
- HttpSecurity.getSharedObject(AuthenticationManagerBuilder.class)로 참조

---

### AuthenticationProvider
- AuthenticationManager로부터 위임받아 실제 사용자 인증 처리 (아이디, 비밀번호, 토큰 등)
- 사용자 신원/자격 증명 검증
- 인증 성공 시: Authentication 객체 반환 (사용자 정보 및 권한 포함)
- 인증 실패 시: AuthenticationException 등 예외 발생

---

### UserDetailsService
- 사용자 상세 정보(아이디, 비밀번호, 권한)를 로드하는 인터페이스
- AuthenticationProvider에서 주로 사용
- 보통 UserRepository를 통해 DB에서 사용자 정보 검색
- 사용자 없을 때 UsernameNotFoundException 발생

---

### UserDetails
- Spring Security에서 사용하는 사용자 정보 인터페이스
- 사용자 ID, 비밀번호, 권한 등 포함
- 인증 완료 후 Authentication 객체에 담겨 보안 컨텍스트에 저장됨
- 기본 구현체: org.springframework.security.core.userdetails.User
 
   
