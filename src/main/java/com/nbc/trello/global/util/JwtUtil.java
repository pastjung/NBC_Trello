package com.nbc.trello.global.util;

import com.nbc.trello.domain.user.User;
import com.nbc.trello.domain.user.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

// JwtUtil : JSON Web Token (JWT)을 생성하고 검증하는 유틸리티 클래스
@Slf4j(topic = "JwtUtil")
@Component  // Bean 으로 사용할 수 있게 설정
public class JwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료 시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private final long ACCESS_TOKEN_TIME = 60 * 1000L; // 1분
    //private final long ACCESS_TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private final long REFRESH_TOKEN_TIME = 600 * 1000L; // 10분
    //private final long REFRESH_TOKEN_TIME = 30 * 24 * 60 * 60 * 1000L; // 30일


    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;    // secretKey 를 암호화해서 사용할 key
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256; // secretKey 를 암호화할 알고리즘

    @PostConstruct  // 런타임에서 Bean 객체가 생성될 때 수행되도록 설정
    public void init() {
        // secretKey 를 HS256 알고리즘으로 암호화
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 인가(Authorization) : HttpRequest 의 header 에서 JWT 토큰 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 인가(Authorization) : JWT 토큰 검증
    public int validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return 0;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
            return 1;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, JWT 토큰 이 비어있습니다.");
        }
        return 2;
    }

    // 인가(Authorization) : JWT 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            // Jwts.parserBuilder() : Jwt 를 파싱하기 위한 파서 빌더 생성
            // .setSigningKey(key) : 생성된 파서 빌더에 서명 키(signing key)를 설정 = JWT 의 서명 확인 용도
            // .build() : 파서 빌더를 사용하여 파서를 생성
            // .parseClaimsJws(token) : 주어진 JWT 토큰을 파싱하여 JWS(JWT with Signature) 객체 생성
            // .getBody() : JWS 객체에서 토큰을 본문(claims)을 가져옴 = 본문은 JWT 에 포함된 사용자 정보를 나타냄
        } catch (ExpiredJwtException e) {
            // JWT 토큰이 만료된 경우 만료된 JWT 토큰의 클레임(claims)을 반환
            return e.getClaims();
        }
    }

    // 인증(Authentication)
    public String createToken(User user) {
        Date date = new Date();

        // 토큰 생성
        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(user.getEmail())                            // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, user.getUserRole().toString())// 사용자 권한
                .setExpiration(new Date(date.getTime() + TOKEN_TIME))   // 만료 시간
                .setIssuedAt(date)                                      // 발급일 : 생성된 시간
                .signWith(key, signatureAlgorithm)                      // 암호화 알고리즘
                .compact();
    }


    // 인증(Authentication) : JWT 토큰 생성 (카카오 로그인)
    public String createKakaoToken(String username, UserRoleEnum userRole) {
        Date date = new Date();

        // 토큰 생성
        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(username)                                   // 사용자 식별자값(ID)
                .claim(AUTHORIZATION_KEY, userRole.toString())          // 사용자 권한
                .setExpiration(new Date(date.getTime() + TOKEN_TIME))   // 만료 시간
                .setIssuedAt(date)                                      // 발급일 : 생성된 시간
                .signWith(key, signatureAlgorithm)                      // 암호화 알고리즘
                .compact();
    }

    // 인증(Authentication) : JWT 토큰 생성
    public String createAccessToken(User user) {
        Date date = new Date();

        // 토큰 생성
        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(user.getEmail())                                    // 사용자 식별자값(ID)
                .claim("userId", user.getId())                               // 사용자 ID
                .claim(AUTHORIZATION_KEY, user.getUserRole().toString())        // 사용자 권한
                .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))    // 만료 시간
                .setIssuedAt(date)                                              // 발급일 : 생성된 시간
                .signWith(key, signatureAlgorithm)                              // 암호화 알고리즘
                .compact();
    }

    // 인증(Authentication) : JWT 토큰 생성
    public String createRefreshToken(User user) {
        Date date = new Date();

        // 토큰 생성
        return BEARER_PREFIX +
            Jwts.builder()
                .setSubject(user.getEmail())                                    // 사용자 식별자값(ID)
                .claim("userId", user.getId())                               // 사용자 ID
                .claim(AUTHORIZATION_KEY, user.getUserRole().toString())        // 사용자 권한
                .setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))   // 만료 시간
                .setIssuedAt(date)                                              // 발급일 : 생성된 시간
                .signWith(key, signatureAlgorithm)                              // 암호화 알고리즘
                .compact();
    }

    // setSubject() : JWT 에 대한 제목
    // setExpiration() : JWT 만료기한 지정 . 파라미터 타입은 java.util.Date
    // setIssuedAt() : JWT 발행 일자 . 파라미터 타입은 java.util.Date
    // signWith() : 서명을 위한 Key (java.security.Key) 객체를 설정
    // compact() : JWT 생성하고 직렬화.

    // setClaims() : JWT 에 포함시킬 Custom Claims 를 추가 (주로 인증된 사용자 정보)
    /* ex. Claim 생성 후 사용
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("userId", user.getUserId());
        claims.put("name", user.getName());
        claims.put("role", user.getRole());
        claims.put("address", user.getAddress());

        setClaims(claims)
     */
}