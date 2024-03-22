package com.nbc.trello.domain.user;

import com.nbc.trello.global.dto.request.SignupRequestDto;
import com.nbc.trello.global.response.CommonResponse;
import com.nbc.trello.global.util.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "사용자")
@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse<Long>> createUser(
        @Valid @RequestBody SignupRequestDto userRequestDto,
        BindingResult bindingResult)
    {
        // @Valid 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            throw new IllegalArgumentException("회원가입 실패: 올바르지 않은 입력 데이터 입니다.");
        }

        log.info("회원가입 시도");
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
            CommonResponse.<Long>builder()
                .statusCode(HttpStatus.CREATED.value())
                .msg("회원가입이 성공하였습니다.")
                .data(userService.signup(userRequestDto))
                .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Long>> logoutUser(
        HttpServletResponse response,
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        userService.logoutUser(response, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.OK.value()).body(CommonResponse.<Long>builder()
            .msg("로그아웃 되었습니다.")
            .statusCode(HttpStatus.OK.value())
            .build()
        );
    }

    @PutMapping
    public ResponseEntity<CommonResponse<UserInfoResponseDto>> updateUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody UserInfoRequestDto requestDto,
        BindingResult bindingResult)
    {
        // @Valid 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            throw new IllegalArgumentException("사용자 정보 수정 실패: 올바르지 않은 입력 데이터 입니다.");
        }

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            CommonResponse.<UserInfoResponseDto>builder()
                .msg("회원정보가 수정되었습니다.")
                .statusCode(HttpStatus.OK.value())
                .data(userService.updateUser(userDetails.getUser(), requestDto))
                .build()
        );
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> deleteUser(
        @AuthenticationPrincipal UserDetailsImpl userDetails)
    {
        userService.deleteUser(userDetails.getUser());

        return ResponseEntity.status(HttpStatus.OK.value()).body(
            CommonResponse.<Void>builder()
                .msg("회원이 삭제되었습니다..")
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
}
