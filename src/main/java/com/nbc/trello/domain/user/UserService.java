package com.nbc.trello.domain.user;

import com.nbc.trello.global.dto.request.SignupRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    /**
     * 회원가입 // @param SignupRequestDto 회원가입 요청 정보
     */

    Long signup(SignupRequestDto userRequestDto);

    @Transactional
    public void logoutUser(HttpServletResponse response, User user);

    @Transactional
    UserInfoResponseDto updateUser(User user, UserInfoRequestDto requestDto);

    @Transactional
    void deleteUser(User user);
}
