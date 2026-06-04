package com.company.agw.domain.user;

import com.company.agw.auth.AuthService;
import com.company.agw.common.exception.BusinessException;
import com.company.agw.common.response.ResponseCode;
import com.company.agw.common.validation.RequestValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthService authService;
    private final RequestValidator requestValidator;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserStatusResponse getUserStatus(UserStatusRequest request) {
        authService.authenticatePassRequest(request.getToken());
        requestValidator.requireText(request.getUserId(), "userId");

        UserEntity user = userMapper.selectUserByUserId(request.getUserId());
        if (user == null) {
            throw new BusinessException(ResponseCode.USER_NOT_FOUND);
        }

        return UserStatusResponse.from(user);
    }

    @Transactional
    public UserInfoResponse getUserInfo(UserInfoRequest request) {
        String userID = request == null ? null : request.getUserID();
        String decodeUserID;

        try {
            decodeUserID = authService.decryptPassUserId(userID);
        } catch (Exception e) {
            return UserInfoResponse.fail(userID, 1400, "Key 무결성 에러");
        }

        if (!hasText(userID) || !isNumeric(decodeUserID)) {
            return UserInfoResponse.fail(userID, 1400, "Key 무결성 에러");
        }

        try {
            PassUserInfoEntity userInfo = userMapper.selectUserPrivateInfobyPass(decodeUserID);
            if (userInfo == null) {
                return UserInfoResponse.notJoined();
            }

            String lastVisitDt = userMapper.getLastVisitAt(decodeUserID);
            userMapper.upsertLastVisitAt(decodeUserID);
            return UserInfoResponse.success(userID, userInfo, lastVisitDt);
        } catch (Exception e) {
            return UserInfoResponse.fail(userID, 1500, "SERVER_ERROR");
        }
    }

    @Transactional
    public SetUserInfoResponse setUserInfo(SetUserInfoRequest request) {
        String userID = request == null ? null : request.getUserID();
        String decodeUserID;

        try {
            decodeUserID = authService.decryptPassUserId(userID);
        } catch (Exception e) {
            return SetUserInfoResponse.fail(userID, 1400, "Key 무결성 에러");
        }

        if (!hasText(userID) || !isNumeric(decodeUserID) || !isValidSetUserInfoRequest(request)) {
            return SetUserInfoResponse.fail(userID, 1400, "Key 무결성 에러");
        }

        try {
            PassUserInfoEntity savedUserInfo = userMapper.selectUserPrivateInfobyPass(decodeUserID);
            if (savedUserInfo == null) {
                return SetUserInfoResponse.notJoined();
            }

            userMapper.updateUserInfoByPass(toPassUserInfoEntity(decodeUserID, request));
            return SetUserInfoResponse.success(userID);
        } catch (Exception e) {
            return SetUserInfoResponse.fail(userID, 1500, "SERVER_ERROR");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isNumeric(String value) {
        return hasText(value) && value.chars().allMatch(Character::isDigit);
    }

    private boolean isValidSetUserInfoRequest(SetUserInfoRequest request) {
        return request != null
                && request.getUserState() != null
                && request.getUserState().size() == 10
                && request.getReferenceFilterValue() != null;
    }

    private PassUserInfoEntity toPassUserInfoEntity(String decodeUserID, SetUserInfoRequest request) {
        PassUserInfoEntity userInfo = new PassUserInfoEntity();
        userInfo.setCustNum(decodeUserID);
        userInfo.setEmailKind(String.valueOf(request.getUserState().get(0)));
        userInfo.setAuthKind(String.valueOf(request.getUserState().get(1)));
        userInfo.setAddrFlag(String.valueOf(request.getUserState().get(2)));
        userInfo.setKisaAgree(String.valueOf(request.getUserState().get(3)));
        userInfo.setUrlHoldoff(String.valueOf(request.getUserState().get(4)));
        userInfo.setPushFlag(request.getUserState().get(5));
        userInfo.setFsecAgree(String.valueOf(request.getUserState().get(6)));
        userInfo.setImpersonateAgree(String.valueOf(request.getUserState().get(7)));
        userInfo.setBlockInternational(String.valueOf(request.getUserState().get(8)));
        userInfo.setBlockRoaming(String.valueOf(request.getUserState().get(9)));
        userInfo.setFilterKind(String.valueOf(request.getReferenceFilterValue()));
        return userInfo;
    }
}
