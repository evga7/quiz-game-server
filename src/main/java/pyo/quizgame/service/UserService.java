package pyo.quizgame.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pyo.quizgame.domain.UserInfo;
import pyo.quizgame.dto.UserInfoDto;
import pyo.quizgame.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean signup(UserInfoDto infoDto) {
        if (isIdExists(infoDto.getUserId())) {
            return false; // 이미 아이디 존재 → 가입 실패
        }

        if ("admin".equals(infoDto.getUserId())) {
            infoDto.setAuth("ROLE_ADMIN");
        } else {
            infoDto.setAuth("ROLE_USER");
        }

        infoDto.setPassword(passwordEncoder.encode(infoDto.getPassword()));

        userRepository.save(UserInfo.builder()
                .userId(infoDto.getUserId())
                .auth(infoDto.getAuth())
                .password(infoDto.getPassword())
                .build());

        return true; // 가입 성공
    }

    @Override
    public UserInfo loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(userId));
    }

    @Transactional(readOnly = true)
    public boolean isIdExists(String userId) {
        return userRepository.findByUserId(userId).isPresent();
    }
}
