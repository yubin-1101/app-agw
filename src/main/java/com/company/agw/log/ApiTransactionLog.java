package com.company.agw.log;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiTransactionLog {

    private String requestUri;
    private String httpMethod;
    private String clientIp;
    private String userIdMasked;
    private String mdnMasked;
    private String retCode;
    private String retMsg;
    private Long elapsedMs;
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;
}
