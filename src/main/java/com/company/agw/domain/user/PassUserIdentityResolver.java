package com.company.agw.domain.user;

import com.company.agw.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassUserIdentityResolver {

    private final AuthService authService;
    private final UserMapper userMapper;

    public PassUserIdentity resolve(String userID) {
        if (!hasText(userID)) {
            throw new IllegalArgumentException("Invalid PASS userID");
        }

        String custNum = authService.decryptPassUserId(userID);
        if (!isNumeric(custNum)) {
            throw new IllegalArgumentException("Invalid PASS userID");
        }

        return new PassUserIdentity(userID, custNum);
    }

    public boolean isJoined(String custNum) {
        return userMapper.selectUserPrivateInfobyPass(custNum) != null;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isNumeric(String value) {
        return hasText(value) && value.chars().allMatch(Character::isDigit);
    }
}
