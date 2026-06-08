package com.company.agw.common.exception;

import com.company.agw.common.response.CommonResponse;
import com.company.agw.common.response.PassResponseCode;
import com.company.agw.common.response.ResponseCode;
import com.company.agw.log.LogUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
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
    public Object handleInvalidRequest(Exception e, HttpServletRequest request) {
        if (isPassExternalV1Request(request)) {
            return handlePassException(request, "INVALID_REQUEST", e, PassResponseCode.INVALID_PARAMETER, LogUtil.LogLevel.WARNING);
        }

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
    public Object handleException(Exception e, HttpServletRequest request) {
        if (isPassExternalV1Request(request)) {
            return handlePassException(request, "UNHANDLED_EXCEPTION", e, PassResponseCode.PROCESS_ERROR, LogUtil.LogLevel.ERROR);
        }

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

    private Object handlePassException(
            HttpServletRequest request,
            String event,
            Exception e,
            PassResponseCode responseCode,
            LogUtil.LogLevel logLevel
    ) {
        request.setAttribute("retCode", String.valueOf(responseCode.getRetCode()));
        request.setAttribute("retMsg", responseCode.getRetMsg());

        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("event", event);
        logData.put("uri", request.getRequestURI());
        logData.put("message", e.getMessage());
        logUtil.write(log, LogUtil.LogType.APPLICATION, logLevel, logData);

        if (logLevel == LogUtil.LogLevel.ERROR) {
            log.error("Unhandled PASS exception stack trace. uri={}", request.getRequestURI(), e);
        }

        return passErrorResponse(request.getRequestURI(), responseCode);
    }

    private boolean isPassExternalV1Request(HttpServletRequest request) {
        return request.getRequestURI() != null && request.getRequestURI().startsWith("/external/pass/v1/");
    }

    private Map<String, Object> passErrorResponse(String uri, PassResponseCode responseCode) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userID", "");
        response.put("retCode", responseCode.getRetCode());
        response.put("retMsg", responseCode.getRetMsg());

        if (uri.endsWith("/getUserfilterWhite") || uri.endsWith("/setUserfilterWhite")) {
            response.put("whiteNUM", List.of());
            response.put("whitePattern", List.of());
            response.put("whiteNUMAddr", List.of());
        } else if (uri.endsWith("/setUserfilterBlack")) {
            response.put("blackNUM", List.of());
            response.put("blackPattern", List.of());
            response.put("blackPrefix", List.of());
        } else if (uri.endsWith("/getUserfilterBlack")) {
            response.put("blackNUM", List.of());
            response.put("blackPattern", List.of());
            response.put("blackPrefix", List.of());
            response.put("prefixPool", List.of());
        }

        return response;
    }
}
