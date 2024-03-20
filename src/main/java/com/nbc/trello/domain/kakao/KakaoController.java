package com.nbc.trello.domain.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nbc.trello.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/api/user/kakao/callback")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response)
        throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드 Service 전달 후 인증 처리 및 JWT 반환
        // 카카오 로그인시 쿠키를 직접 생성해서 JWT 를 넣어주고 브라우저에 자동으로 Set 설정
        String token = kakaoService.kakaoLogin(code);   // JWT 반환

        // Cookie 생성 및 직접 브라우저에 Set
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";    // 메인페이지로 redirect 실행
    }
}
