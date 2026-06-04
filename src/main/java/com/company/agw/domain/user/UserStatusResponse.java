package com.company.agw.domain.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatusResponse {

    private String userId;
    private String serviceStatus;
    private String settingValue;

    public static UserStatusResponse from(UserEntity user) {
        return UserStatusResponse.builder()
                .userId(user.getUserId())
                .serviceStatus(user.getServiceStatus())
                .settingValue(user.getSettingValue())
                .build();
    }
}
