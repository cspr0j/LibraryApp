package com.user.app.server.filter;

import com.user.app.server.service.GetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class CustomHeaderFilter implements Filter {

    private final GetTokenService tokenService;

    private Map<String, String> tokens = new HashMap<>();


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest((HttpServletRequest) request);
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp =(HttpServletResponse) response;

        if (req.getServletPath().equals("/signin")) {
            tokens = tokenService.getTokens(
                    (HttpServletRequest) request,
                    request.getParameter("username"),
                    request.getParameter("password")
            );
        }

        if (req.getServletPath().equals("/users")){
            mutableRequest.putHeader(AUTHORIZATION, "Bearer " + tokens.get("access_token"));

            Cookie cookies = new Cookie("auth", tokens.get("access_token"));
            cookies.setPath("/");
            cookies.setMaxAge(1800);
            resp.addCookie(cookies);
            resp.setHeader(AUTHORIZATION, "Bearer " + tokens.get("access_token"));
        }

        chain.doFilter(mutableRequest, response);
    }
}