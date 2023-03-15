package com.example.jwt.config.jwt;

public interface JwtProperties {

    String SECRET = "JWT SECRET";  // 서버에서 가지고 있는 SECRET Key
    int EXPIRATION_TIME = 60000 * 10;  // 10분 (1/1000초)
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";

}
