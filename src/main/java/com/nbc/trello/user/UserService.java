package com.nbc.trello.user;

import com.nbc.trello.global.dto.request.LoginRequestDto;
import com.nbc.trello.global.dto.request.SignupRequestDto;

public interface UserService {
    /**
     * 회원가입
     * @param requestDto 회원가입 요청 정보
     */
    void signup(SignupRequestDto requestDto);

    /**
     * 로그인
     * @param requestDto 로그인 요청 정보
     * @return 사용자 역할
     */
    UserRoleEnum login(LoginRequestDto requestDto);
}
