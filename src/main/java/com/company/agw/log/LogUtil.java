package com.company.agw.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final ObjectMapper objectMapper;

    public void write(Logger logger, LogType logType, LogLevel logLevel, Object logData) {
        try {
            String message = objectMapper.writeValueAsString(toLogMap(logType, logLevel, logData));
            writeByLevel(logger, logLevel, message);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to serialize log message. logType={}, logLevel={}", logType, logLevel);
        }
    }

    private Map<String, Object> toLogMap(LogType logType, LogLevel logLevel, Object logData) {
        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("logType", logType.name());
        logMap.put("logLevel", logLevel.name());
        logMap.put("createTime", LocalDateTime.now().format(FORMATTER));
        logMap.put("logData", logData);
        return logMap;
    }

    private void writeByLevel(Logger logger, LogLevel logLevel, String message) {
        switch (logLevel) {
            case ERROR -> logger.error(message);
            case WARNING -> logger.warn(message);
            case DEBUG -> logger.debug(message);
            default -> logger.info(message);
        }
    }

    public enum LogType {
        APPLICATION,
        SENSING,
        TRANSACTION
    }

    public enum LogLevel {
        ERROR,
        WARNING,
        INFO,
        DEBUG
    }
}
