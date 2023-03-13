package com.example.jwt.config;

import com.example.jwt.config.jwt.JwtAuthenticationFilter;
import com.example.jwt.filter.MyFilter1;
import com.example.jwt.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);

        // csrf 비활성화
        http.csrf().disable();

        // STATELESS: 서버가 클라이언트의 세션 상태 및 세션 정보를 저장하지 않는 네트워크 프로토콜
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(corsFilter)  // 설정한 CORS 필터 활성화, @CrossOrigin(인증 X), Security 필터에 등록 인증(O)
                .formLogin().disable()  // form 로그인 비활성화
                .httpBasic().disable()  // 기본 http 로그인 비활성화
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))  // form 로그인 비활성화에 따른 UsernamePasswordAuthenticationFilter 추가
                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();
    }

}
