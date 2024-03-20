package com.nbc.trello.domain;

import com.nbc.trello.domain.user.UserInfoDto;
import com.nbc.trello.domain.user.UserRoleEnum;
import com.nbc.trello.domain.user.UserService;
import com.nbc.trello.global.dto.request.SignupRequestDto;
import com.nbc.trello.global.util.UserDetailsImpl;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/api/user/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/api/user/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/api/user/signup")
    public String signup(@Valid SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return "redirect:/api/user/signup";
        }

        userService.signup(requestDto);

        return "redirect:/api/user/login-page";
    }

    // 회원 관련 정보 받기 : basic.js 에서 사용자가 로그인한 상태인지 확인할 때 호출
    @GetMapping("/api/user-info")
    @ResponseBody
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new NullPointerException("accessToken 과 refreshToken 의 사용 기간이 만료되었습니다");
        } else {
            String username = userDetails.getUser().getEmail();
            UserRoleEnum role = userDetails.getUser().getUserRole();
            boolean isAdmin = (role != UserRoleEnum.USER);

            return new UserInfoDto(username, isAdmin);
        }
    }
}