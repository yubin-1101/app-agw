package com.company.agw.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    SUCCESS("0000", "SUCCESS"),
    INVALID_REQUEST("1000", "INVALID_REQUEST"),
    AUTH_FAILED("1100", "AUTH_FAILED"),
    USER_NOT_FOUND("1200", "USER_NOT_FOUND"),
    MESSAGE_NOT_FOUND("1300", "MESSAGE_NOT_FOUND"),
    EXTERNAL_SYSTEM_ERROR("9000", "EXTERNAL_SYSTEM_ERROR"),
    SYSTEM_ERROR("9999", "SYSTEM_ERROR");

    private final String retCode;
    private final String retMsg;
}
