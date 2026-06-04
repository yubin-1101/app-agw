package com.company.agw.common.exception;

import com.company.agw.common.response.ResponseCode;

public class AuthException extends BusinessException {

    public AuthException() {
        super(ResponseCode.AUTH_FAILED);
    }

    public AuthException(String retCode, String retMsg) {
        super(retCode, retMsg);
    }
}
