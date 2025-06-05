## 목차
- [초기화 기본 순서](#초기화-기본-순서)
- [단계별 설명](#단계별-설명)
- [인증 프로세스](#인증-프로세스)
- [인증 아키텍쳐](#인증-아키텍처)
- [인증상태 연속성](#인증-상태-영속성)
- [세션 관리](#세션-관리)
- [예외 처리](#예외-처리)
- [악용 보호](#악용-보호)
- [인가 프로세스](#인가-프로세스)
  



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
- **Filter란?**
 >서블릿 Filter는 웹 애플리케이션에서 클라이언트 요청과 서버 응답을 가공·검사하는 역할을 합니다.  
 >WAS(서블릿 컨테이너)에서 실행 및 종료됩니다.  
- 기본 생명주기 메서드:
  - `init()`: 초기화  
  - `doFilter()`: 요청 전/후 처리 및 다음 필터로 전달  
  - `destroy()`: 필터 제거  

---

-  **DelegatingFilterProxy란?**
>**서블릿 필터 역할 + 스프링의 의존성 주입, AOP 기능과 연동**되도록 설계된 스프링 필터입니다.  
>서블릿 컨테이너와 스프링 ApplicationContext를 연결해주는 **중간 다리 역할**을 합니다.

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
## 인증 프로세스 
**UsernamePasswordAuthenticationFilter : AbstractAuthenticationProcessingFilter -> AttemptAuthentication() -> UsernamePasswordAuthenticationFilter, CustomAuthenticationFilter**
>1.AbstractAuthenticationProcessingFilter 확장한 클래스로 HttpServletRequest에서 제출된 사용자 이름과 비밀번호로 부터 인증을 수행한다. 

>2. 인증 프로스세스 초기화 될떄 로그인 페이지와 로그아웃 페이지 생성을 위한 Default login,logoutFilter가 초기화 된다

>3. RequestMatcher가 클라인트 요청정보가 매칭 되는지 확인 후 false : chaind.doFilter(다음필터로) true: UsernamePasswordAuthenticationToken -> AuthenticationManager(Id,Password) DB랑 비교


**AuthenticationManager** 인증 성공시

>1.UsernamePAsswordAuthenticationToken `UserDetails`와 `Authorities`를 채움.


>2.SeesionAuthenticationStrategy 새로운 로그인을 알리고 세션 관련 작업 수행


>3.SecurityContextHolder Authentication 을 SecurityContext에 설정 세션에 SecurityContex저장(인증 상태 유지)


>4.RememberMeServies  Id,Password 기억하기 기능 



•	RememberMeAuthenticationFilter: 요청이 들어올 때 쿠키가 있으면 인증 복원 시도

•	RememberMeServices: 실제 쿠키 생성/검증 로직 수행


5.ApplicationEventPyublisher 인증 성공 이벤트 게시


6.AuthenticationSuccessHandler 인증 성공 핸들러 호출  (리다이렉트,메시지 등)



### 인증 실패
- `SecurityContextHolder`의 인증 정보 제거  
- `RememberMeServices.loginFail()`로 Remember-Me 정보 초기화  
- `AuthenticationFailureHandler`로 실패 페이지 이동 처리  

---
**RememberMeAuthenticationFilter**
>인증 상태 SecurityContextHolder에 Authertication이 포함되지 않은 경우 실행되는 필터
1.SecurityContextHolder


1️⃣ 인증(Authentication) 저장소
	•	로그인 성공 시, Authentication 객체를 생성하여 SecurityContext에 넣습니다.
	•	예: UsernamePasswordAuthenticationToken 등의 객체.

2️⃣ 스레드 안전성 보장
	•	기본적으로 ThreadLocal 전략을 사용하여 요청 처리 스레드별로 보안 정보를 안전하게 유지합니다.

 #### 프로세스
 **Authentication == null :null 아니면 chain.doFilter**
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

### 인증 아키텍쳐

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

----
## 인증 상태 연송성

**SecurityContextRepository**
>- 스프링 시큐리티에서 사용자가 인증을 한 이후 요청에 대해 계속 사용자의 인증을 유지하기 위해 사용되는 클래스이다
>- 인증 상태의 영속 메커니즘은 사용자가 인증을 하게 되면 해당 사용자의 인증 정보와 권한이 SecurityContext에 저장되고 HttpSession 을 통해 요청 간 영속이 이루어 지는 방식이다
---
**SecurityContextHolderFilter**
>- SecurityContextRepository 를 사용하여 SecurityContext를 얻고 이를 SecurityContextHolder 에 설정하는 필터 클래스이다
>- 이 필터 클래스는 SecurityContextRepository.saveContext()를 강제로 실행시키지 않고 사용자가 명시적으로 호출되어야 SecurityContext를 저장할 수 있는데 이는
SecurityContextPersistenceFilter 와 다른점이다
>- 인증이 지속되어야 하는지를 각 인증 메커니즘이 독립적으로 선택할 수 있게 하여 더 나은 유연성을 제공하고 HttpSession 에 필요할 때만 저장함으로써 성능을 향상시킨다

---
**SecurityContext 생성, 저장, 삭제**
1. 익명 사용자
>•SecurityContextRepository 를 사용하여 새로운 SecurityContext 객체를 생성하여 SecurityContextHolder 에 저장 후 다음 필터로 전달
>•AnonymousAuthenticationFilter 에서 AnonymousAuthenticationToken 객체를 SecurityContext 에 저장
2. 인증 요청
>•SecurityContextRepository 를 사용하여 새로운 SecurityContext 객체를 생성하여 SecurityContextHolder 에 저장 후 다음 필터로 전달
>•UsernamePasswordAuthenticationFilter 에서 인증 성공 후 SecurityContext 에 UsernamePasswordAuthentication 객체를 SecurityContext 에 저장
>•SecurityContextRepository 를 사용하여 HttpSession 에 SecurityContext 를 저장
3. 인증 후 요청
>•SecurityContextRepository 를 사용하여 HttpSession 에서 SecurityContext 꺼내어 SecurityContextHolder 에서 저장 후 다음 필터로 전달>
>•SecurityContext 안에 Authentication 객체가 존재하면 계속 인증을 유지한다
4. 클라이언트 응답 시 공통
>•SecurityContextHolder.clearContext() 로 컨텍스트를 삭제 한다 (스레드 풀의 스레드일 경우 반드시 필요)
---
## 세션 관리

1. 동시 세션 제어
2. 세션 고정 보호
3. 세션 정책
4. SessionManagementFilter & ConcurrentSessionFilter

---
**동시 세션 제어** </br>
• 사용자가 동시에 여러 세션을 생성하는 것을 관리 하는 전략</br>
• 사용자 인증 후에 활성화 된 세션 수가 설정된 maximimSessions값과 비교하여 제어 여부를 결정함 </br>
• 서버는 클라이언트를 식별 못함, SessionId(쿠키)로 식별 하는것으로 보안 
1. 사용자 세션 강제 만료 종료 : 최대 허용 개수 만큼 동시 인증이 가능 하고 그외 이전 사용자 세션 만료
2. 사용자 인증 시도 차단 : 최대 허용 개수 만큼 동시 인증 가능하고 그외 사용자 인증시도 차단

**동시 세션 제어-- SessionManagent()API**
>invalidSessionUrl,expiredUrl(해야함) : 이미 만료된 세션 요청 리다이렉션 url
>maximumSEssions : 사용자 최대 세션수
>maxSeesionsPreventsLogin : true >사용자 인증 시도 차단 , flase >  세션 강제 만료 

**세션 고정 보호 전략**
>•세션 고정 공격은 악의적인 공격자가 사이트에 접근하여 세션을 생성한 다음 다른 사용자가 같은 세션으로 로그인하도록 유도하는 위험을 말한다</br>
>•스프링 시큐리티는 사용자가 로그인할 때 새로운 세션을 생성하거나 세션 ID를 변경함으로써 이러한 공격에 자동으로 대응한다</br>
• changeSessionId()</br>
• newSession()</br>
• migrateSession()</br>
• none()</br>
• 기존 세션을 그대로 사용한다</br>
• 기존 세션을 유지하면서 세션 ID만 변경하여 인증 과정에서 세션 고정 공격을 방지하는 방식이다. 기본 값으로 설정되어 있다</br>
• 새로운 세션을 생성하고 기존 세션 데이터를 복사하지 않는 방식이다(SPRING_SECURITY_ 로 시작하는 속성은 복사한다)</br>
• 새로운 세션을 생성하고 모든 기존 세션 속성을 새 세션으로 복사한다

**세션 생성 정책**
> 인증된 사용자에 대한 세션 생성정책을 설정하여 어떻게 세션을 관리할지 결정 할 수 있으며 이 정책은 SessionCreationPolicy로 설정</br>
**SeesionCreationPolicy**</br>
1. Always: 인증여부에 상관 없이 항상 세션 생성, ForceEagerSessionCreationFIlter 클래스를 추가 구성, 세션을 장제로 생성
2. NEVER: 세션을 생성하지 않지만 애플리케이션(WAS)이 이미 생성한 세션은 사용할 수있다.
3. IF_REQUIRED: 필요한 경우에만 세션 생성, 인증이 필요한 자원에 접근할 떄 세션 생성
4. STATELESS: 세션을 생성,사용 하지 않는다. 인증 필터는 인증완료 후 SecurityContext를 세션에 저장 하지 않는다. JWT 떄 사용 ,CSRF 있을 경우 세션을 생성 CSRF 토큰은 저장, SecurityContxt영속성에 영향을 미치지 않는다.

**SessionManagementFilter**
>사용자 인증 감시, 인증된 경우 세션 고정 보호, 다중 로그인 확인 하는등 세션 관련 활동을 수행하기 위해 세션인증전략(SessionAuthenticationStrategy)을 호출 하는 필터 클래스</br>
>시큐리티 6이상에서는 기본설정 되지 않으며 세션관리API를 설정을 통해 생성

**ConcurrentSessionFilter**
> 각 요청에 대해 SessionRegistry 에서 SessionInfomation을 검색하고 세션이 만료로 표시 되었는지 확인, 만료로 경우 로그아웃(세션무효화)</br>
> 각 요청에 대해 SessionRegistry.rfresghLastRequest를 호풀, 등록된 세션이 항상 마지막 업데이트 날짜/시간을 가지도록 한다.</br>
> SessionManagementFilter 세션 허용개수 초과 등 하면 세션 만료 설정을 한다. 만료 되어있는 경우 로그아웃
---
## 예외 처리
> 필터체인 내에서 발생하는 예외를 의미, 인증예외, 인가 예외가 있다.
> ExceptionTranslationFilter가 사용되며 인증,인가 상태에 따라 로그인 재시도,401,403 코드 응답

**예외 처리 유형**</br>

**AuthenticationException**</br>
1. SecurityContext 인증 정보 삭제 : Authentication 초기화</br>
2. AuthenticationEntryPoint호출 : AuthenticationException 감지 되면 AuthenticationEntryPoint호출 인증 실패를 공통적으로 처리</br>
3. 인증 프로세스의 요청정보 저장 하고 검색: RequestCache, SavedRequest 인증 프로세스 동안 절달되는 요청을 세션, 쿠키에 저장 사용자가 인증 완료한 후 검색 재사용</br>

**AccessDeniedException**
1. AccessDeniedHanler호출 :AccessDeniedException 감지되면 익명사용자 인지 판단, 익명사용자인 경우 인증예외 처리 아닌경우 AccessDeniedHandler 위임
---
## 악용 보호
**CORS(Cross Origin Resource Sharing) 교차 출처 리소스 공유**</br>
1. 보안을 위해 SOP(Same Origin Policy) 사용함
2. 다른 출처의 리소스를 사용할 경우 CORS는 http헤더 를 통해 웹페이지가 다른 출처의 리소스에 접근 할 수 있도록 허가를 구함
3. 출처 비교는 브라우저에 구현된 스펙기준으로 처리, 클라이언트 요청 헤더와 서버 응답헤더를 비교 해서 최종 응답
4. URL 구성중 Protocol ,Host, Port 세가지가 동일한지 확인하면 된다

**Simple Request**
1. Prefilght(예비요청) 없이 자동으로 CORS작동 , Access-Control-Allow-Origin 과 같은 값을 전송 브라우저가 비교
2. GET,POST,HEAD 중 한가지 MEthod를 사용해야 한다.
3. 헤더는 Accept, Accept-Language, Content-Language, Content-Type, DPR, Downlink, Save-Data, Viewport-Width Width 만 가능하고 Custom Header 는 허용되지
않는다
4. Content-type 은 application/x-www-form-urlencoded, multipart/form-data, text/plain 만 가능하다

**Prefilght**
1. 브라우저는 요청을 한번에 보내지 않고, 예비 요청과 본 요청으로 나누어 서버에 전달하는데 브라우저가 예비요청을 보내는 것을 Preflight 라고 하며 이 예비요청의 메소드에는 OPTIONS 가 사용된다</br>
2. 예비요청의 역할은 본 요청을 보내기 전에 브라우저 스스로 안전한 요청인지 확인하는 것으로 요청 사양이 Simple Request 에 해당하지 않을 경우 브라우저가 Preflight Request 을 실행한다</br>

---
**CSRF(Cross Site Request Forgery, 사이트 간 요청 위조)**
>• 웹 애플리케이션의 보안 취약점으로 공격자가 사용자로 하여금 이미 인증된 다른 사이트에 대해 원치 않는 작업을 수행하게 만드는 기법</br>
>• 이 공격은 사용자의 브라우저가 자동으로 보낼 수 있는 인증 정보, 예를 들어 쿠키나 기본 인증 세션을 이용하여 사용자가 의도하지 않은 요청을 서버로 전송하게 만든다</br>
>• 이는 사용자가 로그인한 상태에서 악의적인 웹사이트를 방문하거나 이메일 등을 통해 악의적인 링크를 클릭할 때 발생할 수 있다</br>

**CSRF 기능 활성화**
>• 토큰을 서버에서 생성해서 클라이언트 세션에 저장, 모든 변경 요청에 포함 서버에 이 토큰을 검증 요청 유효성 확인</br>
>• 기본설정은 GET,HEAD,TREAC,OPTIONS 같이 안전 메소드는 무시 , POST, PUT ,DELETE 변경 요청은 CSRF 토큰 검사</br>
>• CSRF토큰은 브라우저에 의해 자동 포함 되지 않는 요청 부분에 위치, 매개변수나 헤더에 존재</br>
>• 쿠키에 토큰 을 넣는것은 위험(자동 전달)

**CSRF 토큰 유지: CsrfTokenRepository(인터페이스)**
>• CsrfToken은 CsrfTokenRepository 를 사용하여 영속화 하며 HttpSessionCsrfTokenRepository 와 CookieCsrfTokenRepository 를 지원한다</br>
>• 두 군데 중 원하는 위치에 토큰을 저장하도록 설정을 통해 지정할 수 있다</br>

**CSRF 토큰처리 :CsrfTokenRequestHandler**
>• CsrfToken 은 CsrfTokenRequestHandler 를 사용하여 토큰을 생성 및 응답하고 HTTP 헤더 또는 요청 매개변수로부터 토큰의 유효성을 검증하도록 한다</br>
>• XorCsrfTokenRequestAttributeHandler 와 CsrfTokenRequestAttributeHandler 를 제공하며 사용자 정의 핸들러를 구현할 수 있다

**CSRF 토큰 지연로딩**
>• 시큐리티는 CsrfToken이 필요할 떄까지 지연 로딩함, Post와 같이 안전하지 않은 메서드 일떄 렌더링

**SameSite**
> sameSite 속성으로 크로스사이트 간 쿠키 제어를 핸들링함
1. Stric: 동일 사이트 모든 요청 쿠키 포함, 크로스 사이트  HTTP 요청 쿠키 포함 안됨</br>
2. Lax(기본설정) : 동일 사이트 요청 및 메소드가 읽기 전용이면 전송 그렇지 않으면 HTTP 요청에 쿠키가 포함되지 않는다. <a> 태그,window.location.replace,302리다이렉트등에는 쿠키가 포함된다. Iframe,img,AJAX 통신은 쿠키가 전송되지 않는다.
3. None: 모든 사이트에 쿠키 전송, HTTS에 의한 Secure 쿠키가 설정되야 함
---
## 인가 프로세스
1. 요청 기반 권한 부여(Request Based Authorization)
2. 메소드 기반 권한 부여(Method Based Authoriztion)
3. 정적 자원 관리
4. 계층적 권한

---
**1.요청 기반 권한 부여(HttpSecurity.authorizeHttpRequests())**
> 요청 기반 권한 부여와 메소드 기반 권한 부여를 통해 자원에 대한 심층적 방어 제공
> 요청 기반 권한 부여는 클라이언트의 요청 즉 HttpServletRequest 에 대한 권한 부여 모델링, HttpSecurity인스턴스 사용 규칙 선언</br>

1. requestMachers: Http 요청, 메소드, URL패턴, 요청 파라미터로 특정 보안 설정
2. 특정 API에만 CSRF보호 적용 하거나 특정 경로에 인증 요구 하지 않는 등 어플리케이션 보안 요구 사항에 맞춰서 유연한 보안 정책 구성
3. 엔드포인트 & 권한 부여 : 위 에서 아래로 부터 나열된 순서대로 처리 예) /admin/** permitAll() 먼저 적용 되면 /admin/db .hasAuthority(admin) 이 적용 안됨<br>
###**❗️엔드 포인트 설정시 좁은 범위 경로를 먼저 정의 하고 보다 큰 범위의 경로를 다음 설정에 정의해야 한다**###
   
