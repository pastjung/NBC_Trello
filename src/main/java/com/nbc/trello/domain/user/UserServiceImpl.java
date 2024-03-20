package com.nbc.trello.domain.user;

import com.nbc.trello.domain.refreshToken.RefreshToken;
import com.nbc.trello.domain.refreshToken.RefreshTokenRepository;
import com.nbc.trello.global.dto.request.SignupRequestDto;
import com.nbc.trello.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "UserServiceImpl")
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private final String ADMIN_TOKEN = "f679d89c320cc4adb72b7647a64ccbe520406dc3ee4578b44bcffbfa7ebbb85e30b964306b6398d3a2d7098ecd1bc203551e356ac5ec4a5ee0c7dc899fb704c5";

    @Override
    public Long signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword()); // 비밀번호 암호화
        String nickname = requestDto.getUsername();

        // DB에 User 가 존재하는지 확인
        // isPresent() : Optional 객체에 값이 존재 여부 확인
//        log.info("회원 존재 확인");
//        if(userRepository.findByEmail(email).isPresent()){
//            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
//        }

        // 새로운 객체 생성
        User user = new User(email, password, nickname, UserRoleEnum.USER);   // role 추가

        // JPA : DB에 새로운 객체 저장
        log.info("회원가입 성공");
        userRepository.save(user);

        return user.getId();
    }

    @Override
    @Transactional
    public void logoutUser(HttpServletResponse response, User user) {
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, null);
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId());

        if (refreshToken != null) {
            log.info("refreshToken 삭제");
            refreshTokenRepository.delete(refreshToken);
        }
    }

    @Override
    @Transactional
    public UserInfoResponseDto updateUser(User user, UserInfoRequestDto requestDto) {
        String username = requestDto.getUsername();
        User savedUser = getUserById(user.getId());

        savedUser.updatedUsername(username);
        return new UserInfoResponseDto(user.getId(), username);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}
