package com.company.agw.log;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserActionLog {

    private String userId;
    private String menuPath;
    private LogAction action;
    private String requestData;
    private String successYn;
    private String clientIp;
    private String errorMessage;
    private LocalDateTime createdAt;
}
