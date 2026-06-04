package com.company.agw.auth;

import com.company.agw.common.exception.AuthException;
import org.springframework.stereotype.Service;

@Service
public class PassSubscriberAuthService {

    public void validateSubscriber(String userId, String mdn) {
        if ((userId == null || userId.isBlank()) && (mdn == null || mdn.isBlank())) {
            throw new AuthException();
        }

        // TODO: PASS 가입자 인증 및 userID/MDN 복호화 규격 확인 필요.
    }
}
