package com.company.agw.log;

import com.company.agw.common.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";
    private static final String SKIP_LOG = "skipLog";

    private final LogUtil logUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            request.setAttribute(SKIP_LOG, true);
            return true;
        }

        request.setAttribute(START_TIME, System.currentTimeMillis());
        logUtil.write(log, LogUtil.LogType.TRANSACTION, LogUtil.LogLevel.INFO, Map.of(
                "event", "API_REQUEST",
                "method", request.getMethod(),
                "uri", request.getRequestURI(),
                "clientIp", request.getRemoteAddr()
        ));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (Boolean.TRUE.equals(request.getAttribute(SKIP_LOG))) {
            return;
        }

        Object startTimeObject = request.getAttribute(START_TIME);
        long startTime = startTimeObject instanceof Long value ? value : System.currentTimeMillis();
        long elapsedMs = System.currentTimeMillis() - startTime;

        String retCode = getAttributeOrDefault(request, "retCode", ResponseCode.SUCCESS.getRetCode());
        String retMsg = getAttributeOrDefault(request, "retMsg", ResponseCode.SUCCESS.getRetMsg());

        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("event", "API_RESPONSE");
        logData.put("method", request.getMethod());
        logData.put("uri", request.getRequestURI());
        logData.put("status", response.getStatus());
        logData.put("retCode", retCode);
        logData.put("elapsedMs", elapsedMs);
        logUtil.write(log, LogUtil.LogType.TRANSACTION, LogUtil.LogLevel.INFO, logData);
    }

    private String getAttributeOrDefault(HttpServletRequest request, String name, String defaultValue) {
        Object value = request.getAttribute(name);
        return value == null ? defaultValue : String.valueOf(value);
    }
}
