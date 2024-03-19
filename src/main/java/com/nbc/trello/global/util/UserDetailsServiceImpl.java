package com.nbc.trello.global.util;

import com.nbc.trello.domain.user.User;
import com.nbc.trello.domain.user.UserRepository;
import com.nbc.trello.domain.user.UserRoleEnum;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// UserDetailsServiceImpl : UserDetailsService 인터페이스를 구현한 서비스 클래스
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    public static final String AUTHORIZATION_KEY = "auth";

    //info 에서 정보를 추출하여 User 생성
    public UserDetails getUserDetails(Claims info) {
        User user = new User();
        user.setId(info.get("userId", Long.class));
        user.setEmail(info.getSubject());

        String userRoleString = info.get(AUTHORIZATION_KEY, String.class);
        user.setUserRole(UserRoleEnum.valueOf(userRoleString));

        return new UserDetailsImpl(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));

        return new UserDetailsImpl(user);
    }
}
