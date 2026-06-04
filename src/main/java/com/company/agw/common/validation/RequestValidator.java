package com.company.agw.common.validation;

import com.company.agw.common.exception.BusinessException;
import com.company.agw.common.response.ResponseCode;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(ResponseCode.INVALID_REQUEST.getRetCode(), fieldName + " is required");
        }
    }
}
