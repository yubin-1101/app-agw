package com.company.agw.log;

import com.company.agw.common.util.MaskingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AopLogAspect {

    private final ObjectMapper objectMapper;
    private final UserActionLogService userActionLogService;

    @Around("@annotation(aopLogInfo)")
    public Object writeUserActionLog(ProceedingJoinPoint joinPoint, AopLogInfo aopLogInfo) throws Throwable {
        boolean success = false;
        String errorMessage = null;

        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } catch (Throwable e) {
            errorMessage = e.getMessage();
            throw e;
        } finally {
            saveUserActionLog(joinPoint, aopLogInfo, success, errorMessage);
        }
    }

    private void saveUserActionLog(ProceedingJoinPoint joinPoint, AopLogInfo aopLogInfo, boolean success, String errorMessage) {
        try {
            HttpServletRequest request = getCurrentRequest();
            Map<String, Object> requestData = extractRequestData(joinPoint);

            UserActionLog userActionLog = UserActionLog.builder()
                    .userId(extractMaskedUserId(requestData))
                    .menuPath(aopLogInfo.menuPath())
                    .action(aopLogInfo.action())
                    .requestData(toJson(requestData))
                    .successYn(success ? "Y" : "N")
                    .clientIp(request == null ? null : request.getRemoteAddr())
                    .errorMessage(errorMessage)
                    .createdAt(LocalDateTime.now())
                    .build();

            userActionLogService.save(userActionLog);
        } catch (Exception e) {
            log.warn("Failed to create user action log. method={}", joinPoint.getSignature().getName());
        }
    }

    private Map<String, Object> extractRequestData(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Map<String, Object> requestData = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (!isLogTarget(parameterAnnotations[i])) {
                continue;
            }

            String parameterName = parameterNames == null ? "arg" + i : parameterNames[i];
            Object value = convertValue(args[i]);
            requestData.put(parameterName, value);
        }
        return requestData;
    }

    private boolean isLogTarget(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequestBody
                    || annotation instanceof ModelAttribute
                    || annotation instanceof RequestPart
                    || annotation instanceof RequestParam
                    || annotation instanceof PathVariable
                    || annotation instanceof RequestHeader
                    || annotation instanceof CookieValue) {
                return true;
            }
        }
        return false;
    }

    private Object convertValue(Object value) {
        if (value == null || isSimpleValue(value)) {
            return value;
        }
        if (value instanceof MultipartFile file) {
            return file.getOriginalFilename();
        }
        if (value instanceof Cookie cookie) {
            return cookie.getName();
        }

        Map<String, Object> fieldMap = new LinkedHashMap<>();
        for (Field field : value.getClass().getDeclaredFields()) {
            Schema schema = field.getAnnotation(Schema.class);
            if (schema != null && schema.writeOnly()) {
                continue;
            }

            field.setAccessible(true);
            String key = schema != null && !schema.description().isBlank() ? schema.description() : field.getName();
            try {
                Object fieldValue = field.get(value);
                fieldMap.put(key, maskIfNeeded(field.getName(), fieldValue));
            } catch (IllegalAccessException e) {
                fieldMap.put(key, null);
            }
        }
        return fieldMap;
    }

    private Object maskIfNeeded(String fieldName, Object value) {
        if (!(value instanceof String stringValue)) {
            return value;
        }
        String lowerFieldName = fieldName.toLowerCase();
        if (lowerFieldName.contains("token")) {
            return MaskingUtils.maskToken(stringValue);
        }
        if (lowerFieldName.contains("mdn")) {
            return MaskingUtils.maskMdn(stringValue);
        }
        if (lowerFieldName.contains("userid") || lowerFieldName.contains("user_id")) {
            return MaskingUtils.maskUserId(stringValue);
        }
        return value;
    }

    private boolean isSimpleValue(Object value) {
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Enum<?>;
    }

    private String extractMaskedUserId(Map<String, Object> requestData) {
        String json = toJson(requestData);
        return json.contains("userId") ? "included_in_request_data" : null;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private HttpServletRequest getCurrentRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            return attributes.getRequest();
        }
        return null;
    }
}
