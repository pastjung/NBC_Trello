package com.nbc.trello.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.trello.domain.refreshToken.RefreshToken;
import com.nbc.trello.domain.refreshToken.RefreshTokenRepository;
import com.nbc.trello.domain.user.User;
import com.nbc.trello.global.response.CommonResponse;
import com.nbc.trello.global.util.JwtUtil;
import com.nbc.trello.global.util.UserDetailsImpl;
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
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final ObjectMapper objectMapper;
    private final RefreshTokenRepository refreshTokenRepository;

    // FilterChain 의 시작 : doFilterInternal
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // JwtUtil 을 이용해 JWT 요청
        String JwtToken = jwtUtil.getJwtFromHeader(request);

        // JWT nullCheck
        if (Objects.nonNull(JwtToken)) {

            // JWT 검증
            int JwtTokenStatus = jwtUtil.validateToken(JwtToken);

            if (JwtTokenStatus == 0) {
                // Claim 정보에 유저 정보 넣기
                Claims info = jwtUtil.getUserInfoFromToken(JwtToken);

                // 사용자 인증 정보 생성 및 인증 처리
                try {
                    setAuthentication(info);
                } catch (Exception e) {
                    // 인증 처리 중 에러 발생시 로그 처리
                    log.error(e.getMessage());
                    return;
                }
            } else if (JwtTokenStatus == 1) {
                // accessToken 의 기간이 만료된 토큰
                try {
                    // Claim 정보에 유저 정보 넣기
                    Claims info = jwtUtil.getUserInfoFromToken(JwtToken);

                    // 사용자 정보 가져오기
                    UserDetailsImpl userDetails
                        = (UserDetailsImpl) userDetailsServiceImpl.loadUserByUsername(
                        info.getSubject());

                    // 가져온 사용자 정보를 이용해 서버에서 refreshToken 탐색
                    RefreshToken refreshToken = refreshTokenRepository.findByUserId(
                        userDetails.getUser().getId());

                    if (refreshToken != null
                        && jwtUtil.validateToken(refreshToken.getRefreshToken()) == 0) {
                        // refreshToken 이 정상 & 만료되지 않은 경우 : accessToken 발급
                        User user = userDetails.getUser();
                        String newAccessToken = jwtUtil.createAccessToken(user);

                        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, newAccessToken);
                    } else {
                        // refreshToken 이 비정상 or 만료된 경우 : refreshToken 삭제 (재로그인 필요)

                        // response 의 body 설정
                        CommonResponse<Void> commonResponse = CommonResponse.<Void>builder()
                            .msg("AccessToken 과 RefreshToken 모두 만료")
                            .statusCode(HttpStatus.UNAUTHORIZED.value())
                            .build();

                        // 응답 값에 status 세팅
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());

                        // Body 가 깨지지 않게 UTF-8로 설정
                        response.setContentType("application/json:charset=UTF-8");

                        // Body 부분에 생성한 responseDto 삽입
                        // objectMapper : 응답 객체를 String 으로 변환
                        response.getWriter().write(objectMapper.writeValueAsString(commonResponse));

                        refreshTokenRepository.delete(refreshToken);
                        log.error("Token 기간 만료");
                        return;
                    }
                } catch (Exception e) {
                    log.error("JWT 인가 오류");
                    return;
                }
            } else {
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
    public void setAuthentication(Claims info) {
        // 현재 사용자의 보안 관련 정보를 저장한 변수 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // userDetails(현재 사용자의 인증 정보) 를 Authentication 의 principal 로 설정
        Authentication authentication = createAuthentication(info);

        // userDetails 를 SecurityContext 에 담기
        context.setAuthentication(authentication);

        // userDetails 를 현재 실행중인 스레드의 SecurityContext 로 설정 : 이제 @AuthenticationPrincipal 로 User 조회 가능
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(Claims info) {
        //userDetails 에 유저의 상세 정보를 넣음 : UserRepository 조회하지 않고 해결
        UserDetails userDetails = userDetailsServiceImpl.getUserDetails(info);

        // userDetails(현재 사용자의 인증 정보) 를 Authentication 의 principal 로 설정
        return new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
    }
}
