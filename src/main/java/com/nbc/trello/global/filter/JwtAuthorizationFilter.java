package com.nbc.trello.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.trello.global.response.CommonResponse;
import com.nbc.trello.global.util.JwtUtil;
import com.nbc.trello.global.util.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter : 요청이 올 때 마다 Filter 를 거치도록 함

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // JwtUtil 을 이용해 JWT 요청
        String JwtToken = jwtUtil.getJwtFromHeader(request);
        // JWT nullCheck
        if (Objects.nonNull(JwtToken)) {
            // JWT 검증
            if (jwtUtil.validateToken(JwtToken) == 0) {
                // Claim 정보에 유저 정보 넣기
                Claims info = jwtUtil.getUserInfoFromToken(JwtToken);

                // 사용자 인증 정보 생성 및 인증 처리
                try {
                    setAuthentication(info.getSubject());
                } catch (Exception e) {
                    // 인증 처리 중 에러 발생시 로그 처리
                    log.error(e.getMessage());
                    return;
                }
            } else{
                // 인증 정보가 존재하지 않을 경우
                String jsonResponse = new ObjectMapper().writeValueAsString(
                    // objectMapper : 응답 객체를 String 으로 변환
                    CommonResponse.<Void>builder()
                        .msg("토큰이 유효하지 않습니다")
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build()
                );

                // response 의 body 설정
                response.setStatus(HttpStatus.BAD_REQUEST.value());         // 응답 상태 코드 설정
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);  // 응답의 Content-Type 설정
                response.setCharacterEncoding("UTF-8");                     // 응답의 문자 인코딩 설정
                response.getWriter().write(jsonResponse);                   // 응답 body에 JSON 데이터 작성

                log.error("Token Error");
                return;
            }
        }
        // 다음 Filter 를 적용할 수 있도록 설정
        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        // 현재 사용자의 보안 관련 정보를 저장한 변수 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // userDetails(현재 사용자의 인증 정보) 를 Authentication 의 principal 로 설정
        Authentication authentication = createAuthentication(username);

        // userDetails 를 SecurityContext 에 담기
        context.setAuthentication(authentication);
        // userDetails 를 현재 실행중인 스레드의 SecurityContext 로 설정 : 이제 @AuthenticationPrincipal 로 User 조회 가능
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        // 사용자명(username) 을 기반으로 사용자(user) 조회 -> user 를 userDetails 에 담기
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
