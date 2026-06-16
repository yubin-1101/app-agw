package com.company.agw.domain.user;

import com.company.agw.common.response.PassResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PassUserIdentityResolver passUserIdentityResolver;
    private final UserMapper userMapper;

    @Transactional
    public GetUserInfoResponse getUserInfo(GetUserInfoRequest request) {
        String userID = request == null ? null : request.getUserID();
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return GetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            PassUserInfoEntity userInfo = userMapper.selectUserPrivateInfobyPass(identity.custNum());
            if (userInfo == null) {
                return GetUserInfoResponse.notJoined();
            }

            String lastVisitDt = userMapper.getLastVisitAt(identity.custNum());
            userMapper.upsertLastVisitAt(identity.custNum());
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
        PassUserIdentity identity;

        try {
            identity = passUserIdentityResolver.resolve(userID);
        } catch (Exception e) {
            return SetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        if (!isValidSetUserInfoRequest(request)) {
            return SetUserInfoResponse.fail(
                    userID,
                    PassResponseCode.INVALID_PARAMETER.getRetCode(),
                    PassResponseCode.INVALID_PARAMETER.getRetMsg()
            );
        }

        try {
            if (!passUserIdentityResolver.isJoined(identity.custNum())) {
                return SetUserInfoResponse.notJoined();
            }

            PassUserState userState = PassUserState.fromRequest(request.getUserState());
            userMapper.updateUserInfoByPass(userState.toEntity(identity.custNum(), request.getReferenceFilterValue()));
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

    private boolean isValidSetUserInfoRequest(SetUserInfoRequest request) {
        return request != null
                && PassUserState.isValidRequest(request.getUserState(), request.getReferenceFilterValue());
    }
}
