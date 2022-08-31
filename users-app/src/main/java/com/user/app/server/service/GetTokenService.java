package com.user.app.server.service;

import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface GetTokenService {
    String createToken(HttpServletRequest request, User user);

    String createRefreshToken(HttpServletRequest request, User user);

    Map<String, String> getTokens(HttpServletRequest request, String username, String password);
}
