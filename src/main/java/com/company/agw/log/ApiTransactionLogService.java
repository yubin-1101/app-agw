package com.company.agw.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiTransactionLogService {

    private final ApiTransactionLogMapper apiTransactionLogMapper;

    @Async
    public void save(ApiTransactionLog apiTransactionLog) {
        try {
            apiTransactionLogMapper.insertApiTransactionLog(apiTransactionLog);
        } catch (Exception e) {
            log.error("Failed to save API transaction log. uri={}", apiTransactionLog.getRequestUri(), e);
        }
    }
}
