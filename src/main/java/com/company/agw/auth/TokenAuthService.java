package com.company.agw.auth;

import com.company.agw.common.exception.AuthException;
import org.springframework.stereotype.Service;

@Service
public class TokenAuthService {

    public void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new AuthException();
        }

        // TODO: API 규격서 기준 토큰 검증 방식 확인 필요.
    }
}
