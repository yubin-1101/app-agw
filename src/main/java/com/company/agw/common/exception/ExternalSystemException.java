package com.company.agw.common.exception;

import com.company.agw.common.response.ResponseCode;

public class ExternalSystemException extends BusinessException {

    public ExternalSystemException() {
        super(ResponseCode.EXTERNAL_SYSTEM_ERROR);
    }

    public ExternalSystemException(String retCode, String retMsg) {
        super(retCode, retMsg);
    }
}
