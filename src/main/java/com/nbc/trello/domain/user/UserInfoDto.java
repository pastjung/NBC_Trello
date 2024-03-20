package com.nbc.trello.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfoDto {

    String username;
    boolean isAdmin;
}