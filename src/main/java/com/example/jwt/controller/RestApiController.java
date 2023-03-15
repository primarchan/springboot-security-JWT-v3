package com.example.jwt.controller;

import com.example.jwt.config.auth.PrincipalDetails;
import com.example.jwt.dto.UserDto;
import com.example.jwt.model.User;
import com.example.jwt.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final UserLoginService userLoginService;

    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }

    @PostMapping("/token")
    public String token() {
        return "<h1>token</h1>";
    }

    @PostMapping("/join")
    public String join(@RequestBody UserDto userDto) {
        return userLoginService.join(userDto);
    }

    // user, manager, admin 권한 접근 가능
    @GetMapping("/api/v1/user")
    public String user(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        log.info("Authentication : {}", principalDetails.getUsername());
        return "user";
    }

    // manager, admin 권한 접근 가능
    @GetMapping("/api/v1/manager")
    public String manager() {
        return "manager";
    }

    // admin 권한 접근 가능
    @GetMapping("/api/v1/admin")
    public String admin() {
        return "admin";
    }

}
