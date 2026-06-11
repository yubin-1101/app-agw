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
        return decryptFileUrl(fileName, custNum);
    }

    private String decryptFileUrl(String fileUrl, String key) {
        String fileExt = getFileExt(fileUrl);
        if (hasText(fileExt)) {
            String encryptedBody = fileUrl.substring(0, fileUrl.lastIndexOf('.'));
            return aesManager.decryptWithKey(encryptedBody, key) + "." + fileExt;
        }

        return aesManager.decryptWithKey(fileUrl, key);
    }

    private String getFileExt(String fileName) {
        if (!hasText(fileName)) {
            return "";
        }

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(dotIndex + 1);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
