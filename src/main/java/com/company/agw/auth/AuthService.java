package com.company.agw.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AesManager aesManager;
    private final TokenAuthService tokenAuthService;
    private final PassSubscriberAuthService passSubscriberAuthService;
    private final InternalApiAuthService internalApiAuthService;

    public void authenticatePassRequest(String token) {
        tokenAuthService.validateToken(token);
    }

    public void authenticateSubscriber(String userId, String mdn) {
        passSubscriberAuthService.validateSubscriber(userId, mdn);
    }

    public void authenticateInternalRequest(String apiKey) {
        internalApiAuthService.validateApiKey(apiKey);
    }

    public String decryptPassUserId(String userID) {
        return aesManager.decrypt(userID);
    }
}
