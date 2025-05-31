#-1일차-

---
### 🔷 1️⃣ 기본 순서

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
## 🔷 Filter란?

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
   

   
