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

    public String decryptPassFileName(String fileName, String custNum) {
        String fileExt = getFileExt(fileName);

        if (!fileExt.isBlank()) {
            String encryptedBody = fileName.substring(0, fileName.lastIndexOf("."));
            return aesManager.decryptWithKey(encryptedBody, custNum) + "." + fileExt;
        }

        return aesManager.decryptWithKey(fileName, custNum);
    }

    private String getFileExt(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex < 0 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1);
    }
}
