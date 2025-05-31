#-1일차-

---
### 기본 SrpingSecurity filter 생성 순서
1.AutoConfiguration -> 2.SecurityBuilder,3.SecurityConfiguration ->4.HttpSecurityConfiguration->5.httpSecurity build(HttpSecurityConfiguration.java)->5.SecurityFilterChain ->6.WebSecurityConfiguration-> 7.WebSecurity -> FilterChainProxy

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
   

   
