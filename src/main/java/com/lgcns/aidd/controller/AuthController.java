package com.lgcns.aidd.controller;

import com.lgcns.aidd.dto.request.LoginRequest;
import com.lgcns.aidd.dto.request.RefreshTokenRequest;
import com.lgcns.aidd.dto.response.LoginResponse;
import com.lgcns.aidd.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = accountService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        if(request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }
        String newAccessToken = accountService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(Map.of("AccessToken", newAccessToken));
    }
}
