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
### Filter서블릿 필터는 웹 어플리케이션에서 클라이언트의 요청과 서버의 응답을 가공,검사하는데 사용되는 구성요서 WAS(서블릿컨테이너) 에서 실행 되고 종료
1.Filter init(초기화),doFilter(요청정,다음 필터로 전달, 요청후 처리),destory(필터제거)

### DelegationFilterProxy 스프링에서 사용되는 서블릿 필터, 서블릿컨테이너와 스프링 어플리케이션 컨테스트간의 연결고리 하는 필터
1. 스프링 기능을 사용하긴 위한 (DI,AOP) 필터
2. 서블릿필터 기능을 수행하는 동시에 스프링 의존성 주입 및 관리 기능과 연동되도록 설계된 필터
3. SrpingSecurityFilterChain 이름으로 생성된 빈을 ApplicationContext에서 찾아 요청을 위임
4. FilterChainProxy 가 URL 정보를 기준으로 적절한 SecurityFilterChain을 선택하여 필터 호출  (순서대로 호출)
   
 ✅ 스프링 시큐리티와 DelegatingFilterProxy의 연결

🔷 동작 순서

1️⃣ 톰캣이 요청을 받으면 → 등록된 FilterChain 순서대로 doFilter() 호출


2️⃣ DelegatingFilterProxy의 doFilter()가 실행됨


3️⃣ 내부적으로 ApplicationContext에서 “springSecurityFilterChain” 이름의 빈을 찾아서


4️⃣ 그 빈의 doFilter()를 대신 실행
   

   
