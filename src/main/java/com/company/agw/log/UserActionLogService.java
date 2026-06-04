package com.company.agw.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActionLogService {

    private final UserActionLogMapper userActionLogMapper;

    @Async
    public void save(UserActionLog userActionLog) {
        try {
            userActionLogMapper.insertUserActionLog(userActionLog);
        } catch (Exception e) {
            log.error("Failed to save user action log. menuPath={}, action={}",
                    userActionLog.getMenuPath(), userActionLog.getAction(), e);
        }
    }
}
