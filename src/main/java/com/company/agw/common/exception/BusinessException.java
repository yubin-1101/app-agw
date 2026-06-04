package com.company.agw.common.exception;

import com.company.agw.common.response.ResponseCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final String retCode;
    private final String retMsg;

    public BusinessException(ResponseCode responseCode) {
        this(responseCode.getRetCode(), responseCode.getRetMsg());
    }

    public BusinessException(String retCode, String retMsg) {
        super(retMsg);
        this.retCode = retCode;
        this.retMsg = retMsg;
    }
}
