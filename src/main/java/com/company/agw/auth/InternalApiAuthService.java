package com.company.agw.auth;

import com.company.agw.common.exception.AuthException;
import org.springframework.stereotype.Service;

@Service
public class InternalApiAuthService {

    public void validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AuthException();
        }

        // TODO: 내부 API 인증 방식 확인 필요.
    }
}
