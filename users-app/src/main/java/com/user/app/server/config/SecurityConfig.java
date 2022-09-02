package com.user.app.server.config;

import com.user.app.server.filter.CustomAuthenticationFilter;
import com.user.app.server.filter.CustomAuthorizationFilter;
import com.user.app.server.filter.CustomHeaderFilter;
import com.user.app.server.service.GetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService detailsService;
    private final GetTokenService tokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception {
        return auth.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests().antMatchers("/login", "/register", "/signin", "/index", "/").permitAll()
                .and()
                    .authorizeRequests()
                    .antMatchers("/users", "/api/users", "/api/users/save").hasAuthority("USER")
                .and()
                    .authorizeRequests().anyRequest().authenticated()
                .and()
                    .logout().logoutUrl("/logout")
                    .logoutSuccessUrl("/login")
                    .deleteCookies("auth")
                .and()
                    .userDetailsService(detailsService)
                    .addFilterBefore(new CustomHeaderFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                    .addFilterAfter(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilter(new CustomAuthenticationFilter(tokenService, customAuthenticationProvider()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider customAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(detailsService);

        return provider;
    }
}
