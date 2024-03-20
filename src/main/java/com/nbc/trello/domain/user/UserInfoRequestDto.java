package com.nbc.trello.domain.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserInfoRequestDto {

    @NotNull(message = "Null 값 입력 불가")
    @Pattern(regexp = "^[a-z0-9]{4,10}", message = "이메일은 4~10자, 영문자 및 숫자로 구성해주세요")
    private String email;

    @NotNull(message = "Null 값 입력 불가")
    private String username;
}