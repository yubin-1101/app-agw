package com.company.agw.common.exception;

import com.company.agw.common.response.CommonResponse;
import com.company.agw.common.response.ResponseCode;
import com.company.agw.log.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LogUtil logUtil;

    @ExceptionHandler(BusinessException.class)
    public CommonResponse<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        request.setAttribute("retCode", e.getRetCode());
        request.setAttribute("retMsg", e.getRetMsg());
        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("event", "BUSINESS_EXCEPTION");
        logData.put("uri", request.getRequestURI());
        logData.put("retCode", e.getRetCode());
        logData.put("retMsg", e.getRetMsg());
        logUtil.write(log, LogUtil.LogType.APPLICATION, LogUtil.LogLevel.WARNING, logData);
        return CommonResponse.fail(e.getRetCode(), e.getRetMsg());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            HttpMessageNotReadableException.class
    })
    public CommonResponse<Void> handleInvalidRequest(Exception e, HttpServletRequest request) {
        ResponseCode responseCode = ResponseCode.INVALID_REQUEST;
        request.setAttribute("retCode", responseCode.getRetCode());
        request.setAttribute("retMsg", responseCode.getRetMsg());
        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("event", "INVALID_REQUEST");
        logData.put("uri", request.getRequestURI());
        logData.put("message", e.getMessage());
        logUtil.write(log, LogUtil.LogType.APPLICATION, LogUtil.LogLevel.WARNING, logData);
        return CommonResponse.fail(responseCode);
    }

    @ExceptionHandler(Exception.class)
    public CommonResponse<Void> handleException(Exception e, HttpServletRequest request) {
        ResponseCode responseCode = ResponseCode.SYSTEM_ERROR;
        request.setAttribute("retCode", responseCode.getRetCode());
        request.setAttribute("retMsg", responseCode.getRetMsg());
        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("event", "UNHANDLED_EXCEPTION");
        logData.put("uri", request.getRequestURI());
        logData.put("message", e.getMessage());
        logUtil.write(log, LogUtil.LogType.APPLICATION, LogUtil.LogLevel.ERROR, logData);
        log.error("Unhandled exception stack trace. uri={}", request.getRequestURI(), e);
        return CommonResponse.fail(responseCode);
    }
}
