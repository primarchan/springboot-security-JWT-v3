package com.example.jwt.config.auth;

import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * http://localhost:8080/login -> form 로그인을 비활성화 했기 때문에 Spring Security 에서 가로채는 /login 에서 동작하지 않음
 * 따라서 Spring Security 필터에 UsernamePasswordAuthenticationFilter 을 등록
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("PrincipalDetailsService 의 loadUserByUsername()");
        User userEntity = userRepository.findByUsername(username);

        return new PrincipalDetails(userEntity);
    }
}
