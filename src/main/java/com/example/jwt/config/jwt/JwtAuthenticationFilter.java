package com.example.jwt.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.config.auth.PrincipalDetails;
import com.example.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

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

        // 1. username, password 받음
        try {
            /*
            BufferedReader br = request.getReader();

            String input = null;

            while ((input = br.readLine()) != null) {
                log.info(input);
            }
             */

            ObjectMapper objectMapper = new ObjectMapper();
            User user = objectMapper.readValue(request.getInputStream(), User.class);
            log.info(String.valueOf(user));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());

            // PrincipalDetailsService 의 loadByUsername() 메서드가 실행된 후, 정상이면 authentication 이 리턴됨.
            // DB 에 있는 username 과 password 가 일치
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

            log.info(String.valueOf("로그인 완료됨 : {}"), principalDetails.getUser().getUsername());  // 로그인이 되었다는 의미

            // 인증 토큰을 통해 반환받은 authentication 객체가 session 영역에 저장해야하고 그 방법이 return 해주면 됨
            // return 의 이유는 권한 관리를 security 가 대신 해주기 때문에 편하려고 하는 것
            // 굳이 JWT 토큰을 사용하면서 session 을 만들 이유가 없음
            // 그러나 권한 처리 때문에 session 에 넣어 줌

            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("=====================================================");

        // 2. 정상인지 로그인 시도 -> authenticationManager 로 로그인 시도를 하면 PrincipalDetailsService 의 loadUserByUsername 메서드 호출
        // 3. PrincipalDetails 를 세션에 저장 (Spring Security 의 권한 관리를 위해)
        // 4. JWT 토큰을 생성하여 응답
        return null;
    }

    // attemptAuthentication 메서드 실행 이후, 인증이 정상적으로 되었으면 successfulAuthentication 메서드 실행 됨
    // JWT 토큰을 생성해서 request 요청한 사용자에게 JWT 토큰을 response 해주면 됨
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("successfulAuthentication 실행 됨 : 인증 완료되었다는 의미");

        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();

        // HMAC512 Hash 암호 방식
        String jwtToken = JWT.create()
                .withSubject(principalDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("username", principalDetails.getUser().getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
    }
}
