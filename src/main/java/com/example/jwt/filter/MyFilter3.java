package com.example.jwt.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class MyFilter3 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");

        /**
         * 테스트용 임시 토큰 : cos
         * 클라이언트로부터 ID, PW 가 정상적으로 들어와서 로그인이 완료되면 토큰을 생성하여 응답
         * 요청할 때 마다 header 에 Authorization 에 Value 로 토큰을 가자고 옴
         * 토큰이 넘어오면 이 토큰이 서버에서 생성한 토큰이 맞는지 검증 (RSA, HS256)
         */
        if (req.getMethod().equals("POST")) {
            log.info("POST 요청 됨");
            String headerAuth = req.getHeader("Authorization");
            log.info(headerAuth);
            log.info("필터 3");
            if (headerAuth.equals("cos")) {
                chain.doFilter(req, res);
            } else {
                PrintWriter out = res.getWriter();
                out.println("인증 안됨");
            }
        }
    }

}
