package com.company.agw.domain.user;

import com.company.agw.auth.AuthService;
import com.company.agw.common.response.PassResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthService authService;
    private final UserMapper userMapper;

    @Transactional
    public GetUserInfoResponse getUserInfo(GetUserInfoRequest request) {
        String userID = request == null ? null : request.getUserID();
        String decodeUserID;

        try {
            decodeUserID = authService.decryptPassUserId(userID);
        } catch (Exception e) {
            return GetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!hasText(userID) || !isNumeric(decodeUserID)) {
            return GetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            PassUserInfoEntity userInfo = userMapper.selectUserPrivateInfobyPass(decodeUserID);
            if (userInfo == null) {
                return GetUserInfoResponse.notJoined();
            }

            String lastVisitDt = userMapper.getLastVisitAt(decodeUserID);
            userMapper.upsertLastVisitAt(decodeUserID);
            return GetUserInfoResponse.success(userID, userInfo, lastVisitDt);
        } catch (Exception e) {
            return GetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
        }
    }

    @Transactional
    public SetUserInfoResponse setUserInfo(SetUserInfoRequest request) {
        String userID = request == null ? null : request.getUserID();
        String decodeUserID;

        try {
            decodeUserID = authService.decryptPassUserId(userID);
        } catch (Exception e) {
            return SetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!hasText(userID) || !isNumeric(decodeUserID) || !isValidSetUserInfoRequest(request)) {
            return SetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            PassUserInfoEntity savedUserInfo = userMapper.selectUserPrivateInfobyPass(decodeUserID);
            if (savedUserInfo == null) {
                return SetUserInfoResponse.notJoined();
            }

            PassUserState userState = PassUserState.fromRequest(request.getUserState());
            userMapper.updateUserInfoByPass(userState.toEntity(decodeUserID, request.getReferenceFilterValue()));
            return SetUserInfoResponse.success(userID);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return SetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.PROCESS_ERROR.getRetCode(),
                    PassResponseCode.PROCESS_ERROR.getRetMsg()
            );
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
                && PassUserState.isValidRequest(request.getUserState(), request.getReferenceFilterValue());
    }
}
