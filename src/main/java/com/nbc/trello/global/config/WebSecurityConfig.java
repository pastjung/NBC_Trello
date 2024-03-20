package com.nbc.trello.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.trello.domain.refreshToken.RefreshTokenRepository;
import com.nbc.trello.global.filter.JwtAuthenticationFilter;
import com.nbc.trello.global.filter.JwtAuthorizationFilter;
import com.nbc.trello.global.util.JwtUtil;
import com.nbc.trello.global.util.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // Bean 사용 정의
@EnableWebSecurity  // Spring Security 사용 정의
@RequiredArgsConstructor    // 모든 필드를 가지는 생성자 생성
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ObjectMapper objectMapper;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshTokenRepository refreshTokenRepository;


    // Password Encoder : 기존 Default 로 생성된 Password Encoder 대신 BCryptPasswordEncoder 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JwtAuthorizationFilter 수동 주입 : Filter 실행(JwtAuthorizationFilter, JwtAuthenticationFilter 적용)
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsServiceImpl, objectMapper,
            refreshTokenRepository);
    }

    // AuthenticationManager 수동 주입
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    // JwtAuthenticationFilter 수동 주입
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil,
            refreshTokenRepository);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    // securityFilterChain : 기존에 설정되어 있는 Spring Security 를 원하는대로 동작할 수 있도록 다룰수 있게 제공해주는 Bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // CSRF 설정 (사이트 간의 요청 위조) : CSRF 기능은 사용 안하므로 disable
        httpSecurity.csrf((csrf) -> csrf.disable());

        // 기본 설정인 "Session 방식"은 사용하지 않고 "JWT 방식"을 사용하기 위한 설정
        // STATELESS : 스프링 시큐리티가 생성하지 않고 존재해도 사용안함.
        // Never : 생성하지 않지만 이미 존재하면 사용
        // If_Required : 필요시 생성 ( 기본값 )
        // Always : 항상 세션 생성
        httpSecurity.sessionManagement((sessionManagement) ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.authorizeHttpRequests((authorizeHttpRequests) ->
            authorizeHttpRequests
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()                              // resources 접근 허용 설정, permitAll() : 접근 허가
                .requestMatchers("/").permitAll()       // 메인 페이지 접근 허용
                .requestMatchers("/**").permitAll()     // 모든 페이지 접근 허가
                .anyRequest()
                .authenticated());           // anyRequest() : 위 설정 이외 모두, authenticated() : jwt 인증 필요함

        // 로그인 사용
        httpSecurity.formLogin((formLogin) ->
            formLogin.loginPage("/login").permitAll()
        );

        // Filter 관리
        httpSecurity.addFilterBefore(jwtAuthorizationFilter(),
            JwtAuthenticationFilter.class);                  // JWT 검증 및 인가
        httpSecurity.addFilterBefore(jwtAuthenticationFilter(),
            UsernamePasswordAuthenticationFilter.class);    // 로그인 및 JWT 생성

        return httpSecurity.build();
    }
}
