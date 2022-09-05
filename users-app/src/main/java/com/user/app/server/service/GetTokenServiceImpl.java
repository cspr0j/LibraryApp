package com.user.app.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GetTokenServiceImpl implements GetTokenService {

    private final PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Override
    public String createToken(HttpServletRequest request, User user) {
        Algorithm algorithm = Algorithm.HMAC512("my_secret_key_10210_oqpowqkq192199qkkwoxa");

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim(
                        "roles",
                        user.getAuthorities()
                                .stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList())
                )
                .sign(algorithm);
    }


    @Override
    public String createRefreshToken(HttpServletRequest request, User user) {
        Algorithm algorithm = Algorithm.HMAC512("my_secret_key_10210_oqpowqkq192199qkkwoxa");

        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 10000))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
    }

    @Override
    public Map<String, String> getTokens(HttpServletRequest request, String username, String password) {
        com.user.app.server.model.User myUser = userService.getUser(username);

        if (myUser == null || password == null || !passwordEncoder.matches(password, myUser.getPassword()) ) {
            throw new BadCredentialsException("Bad Credentials!");
        }

        User user = (User) userDetailsService.loadUserByUsername(username);
        String accessToken = createToken(request, user);

        String refreshToken = createRefreshToken(request, user);
        Map<String, String> tokens = new HashMap<>();

        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return tokens;
    }
}
