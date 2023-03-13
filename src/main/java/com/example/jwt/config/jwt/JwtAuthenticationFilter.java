package com.example.jwt.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring Security 에 존재하는 UsernamePasswordAuthenticationFilter
 * login 요청해서 username, password 전송하면 (POST)
 * UsernamePasswordAuthenticationFilter 가 동작
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    /**
     * /login 요청을 하면 로그인 시도를 위해서 실행되는 메서드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtAuthenticationFilter : 로그인 시도 중");

        /**
         * 1. username, password 받음
         * 2. 정상인지 로그인 시도
         * -> authenticationManager 로 로그인 시도를 하면 PrincipalDetailsService 의 loadUserByUsername 메서드 호출
         * 3. PrincipalDetails 를 세션에 저장 (Spring Security 의 권한 관리를 위해)
         * 4. JWT 토큰을 생성하여 응답
         */

        return super.attemptAuthentication(request, response);
    }

}
