package srpingsecurity.security;

import jakarta.servlet.http.HttpServletRequest;

public class CustomRequestMatcher implements org.springframework.security.web.util.matcher.RequestMatcher{

    private final String urlPattern;

    public CustomRequestMatcher(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return request.getRequestURI().startsWith(urlPattern);
    }
}
