package com.nbc.trello.User;

import com.nbc.trello.global.dto.request.LoginRequestDto;
import com.nbc.trello.global.dto.request.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String ADMIN_TOKEN = "f679d89c320cc4adb72b7647a64ccbe520406dc3ee4578b44bcffbfa7ebbb85e30b964306b6398d3a2d7098ecd1bc203551e356ac5ec4a5ee0c7dc899fb704c5";

    @Override
    public void signup(SignupRequestDto requestDto){
    }

    // 로그인 : 인증(Authentication)
    @Override
    @PostMapping("/login")
    public UserRoleEnum login(@RequestBody LoginRequestDto requestDto){
        return null;
    }
}
