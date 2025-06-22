package com.lgcns.aidd.service;

import com.lgcns.aidd.dto.request.LoginRequest;
import com.lgcns.aidd.dto.response.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {
    public LoginResponse login(LoginRequest request);

    public String refreshAccessToken(String refreshToken);
}
