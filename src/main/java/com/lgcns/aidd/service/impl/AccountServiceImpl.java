package com.lgcns.aidd.service.impl;

import com.lgcns.aidd.dto.request.LoginRequest;
import com.lgcns.aidd.dto.response.LoginResponse;
import com.lgcns.aidd.repository.AccountRepository;
import com.lgcns.aidd.security.CustomUserDetailsService;
import com.lgcns.aidd.security.JwtService;
import com.lgcns.aidd.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    private final AccountRepository accountRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        String email = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
        return new LoginResponse(accessToken, refreshToken, email, role);
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if(!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        return jwtService.generateAccessToken(userDetails);
    }

}
