package com.example.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.config.auth.PrincipalDetails;
import com.example.jwt.model.User;
import com.example.jwt.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security 가 가지고 있는 Filter 중 BasicAuthenticationFilter 가 존재
 * 권한이나 인증이 필요한 특정 주소를 요청했을 때, 위 Filter 를 반드시 거치게 되어 있음
 * 만약에 권한, 인증이 필요한 주소가 아니라면 위 Filter 를 거치지 않음
 */

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    // 인증, 권한이 필요한 URL 로 요청이 왔을 때, 해당 Filter 를 타게 됨
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("인증, 권한이 필요한 주소 요청됨");

        String jwtHeader = request.getHeader("Authorization");
        log.info("JWT HEADER : {}", jwtHeader);

        // header 가 있는지 확인
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        // JWT 토큰을 검증을 해서 정상적인 사용자인지 확인
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");

        String username = JWT.require(Algorithm.HMAC512("JWT SECRET"))
                .build()
                .verify(jwtToken)
                .getClaim("username")
                .asString();

        // 서명이 정상적으로 됨
        if (username != null) {
            log.info("username 정상");
            User userEntity = userRepository.findByUsername(username);
            log.info("userEntity : {}", userEntity);

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
            log.info("principalDetails : {}", principalDetails.getUsername());

            // 검증을 위한 Authentication 객체 생성
            // JWT 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어 준다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            // Spring Security Session 에 접근하여 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

}
